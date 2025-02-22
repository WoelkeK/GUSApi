package pl.woelke.gus_api.soap.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.woelke.gus_api.rest.exception.SOAPProtocolException;
import pl.woelke.gus_api.soap.response.SOAPDataResponse;
import pl.woelke.gus_api.soap.response.SOAPLoginResponse;
import pl.woelke.gus_api.soap.response.SOAPLogoutResponse;
import pl.woelke.gus_api.soap.response.model.SOAPDataRoot;
import pl.woelke.gus_api.soap.util.SoapEnvelopeBuilder;
import pl.woelke.gus_api.soap.util.SoapXmlParser;


import java.io.StringReader;

@Service
@Slf4j
public class GusSoapService implements SOAPService {

    private final WebClient webClient;
    private final SoapXmlParser xmlParser;

    @Value("${gus.soap.api-key}")
    private static String API_KEY;

    public GusSoapService(WebClient webClient, SoapXmlParser xmlParser) {
        this.webClient = webClient;
        this.xmlParser = xmlParser;
    }

    public String login(String api_key) throws SOAPProtocolException {

        if (!isNotNullOrEmpty(api_key)) {
            throw new IllegalArgumentException("API Key cannot be null or empty.");
        }
        updateApiKey(api_key);

        SoapEnvelopeBuilder builder = new SoapEnvelopeBuilder();
        String loginEnvelope = builder.createLoginEnvelope(API_KEY);

        try {
            String response = getResponse(loginEnvelope, null);
            SOAPLoginResponse soapResponse = xmlParser.parse(response, SOAPLoginResponse.class);
            return soapResponse.getBody().getLoginResponse().getLoginResult();

        } catch (Exception ex) {
            throw new SOAPProtocolException("Error during login: " + ex.getMessage());
        }
    }

    public String logout(String sessionId) throws SOAPProtocolException {

        if (!isNotNullOrEmpty(sessionId)) {
            throw new IllegalArgumentException("Session ID cannot be null or empty.");
        }

        SoapEnvelopeBuilder builder = new SoapEnvelopeBuilder();
        String logoutEnvelope = builder.createLogoutEnvelope(sessionId);
        try {
            String response = getResponse(logoutEnvelope, sessionId);
            SOAPLogoutResponse soapResponse = xmlParser.parse(response, SOAPLogoutResponse.class);
            boolean logoutResult = soapResponse.getBody().getLogoutResponse().isLogoutResult();
            return logoutResult ? "Sesja SOAP wylogowana prawidłowo" : "Nieudane wylogowanie sesji SOAP";

        } catch (Exception ex) {
            throw new SOAPProtocolException("Error during login: " + ex.getMessage());
        }
    }

    public SOAPDataRoot getData(String sessionId, String taxNo) throws SOAPProtocolException {

        SoapEnvelopeBuilder builder = new SoapEnvelopeBuilder();
        String dataEnvelope = builder.createDataRequestEnvelopeWithTaxNo(taxNo);

        try {
            String response = getResponse(dataEnvelope, sessionId);
            SOAPDataResponse soapResponse = xmlParser.parse(response, SOAPDataResponse.class);
            SOAPDataRoot dataRoot = parseEncodedResponse(soapResponse);
            log.debug("Data response: {}", response);
            return dataRoot;

        } catch (Exception ex) {
            throw new SOAPProtocolException("Error during login: " + ex.getMessage());
        }
    }

    // Metoda GetValue nie działą - do poprawy koperta
    public String getStatus(String sessionId) throws SOAPProtocolException {

        if (!isNotNullOrEmpty(sessionId)) {
            throw new IllegalArgumentException("Session ID cannot be null or empty.");
        }

//        String parameterName = "StatusSesji";
//
//        // Tworzymy SOAP payload dla metody GetValue
//        String soapPayload =
//                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" " +
//                        "xmlns:ns=\"http://CIS/BIR/2014/07\">\n" +
//                        "    <soap:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">\n" +
//                        "        <wsa:To>" + SOAP_URL + "</wsa:To>\n" +
//                        "        <wsa:Action>http://CIS/BIR/2014/07/IUslugaBIR/GetValue</wsa:Action>\n" +
//                        "    </soap:Header>\n" +
//                        "    <soap:Body>\n" +
//                        "        <ns:GetValue>\n" +
//                        "            <ns:pNazwaParametru>" + parameterName + "</ns:pNazwaParametru>\n" +
//                        "        </ns:GetValue>\n" +
//                        "    </soap:Body>\n" +
//                        "</soap:Envelope>";

        SoapEnvelopeBuilder builder = new SoapEnvelopeBuilder();
        String statusEnvelope = builder.createStatusEnvelope();

        log.info("Status envelope: {}", statusEnvelope);
        return null;
    }


    private String getResponse(String envelope, String sessionId) throws SOAPProtocolException {
        String response = webClient.post()
                .bodyValue(envelope)
                .header("sid", sessionId)
                .retrieve()
                .bodyToMono(String.class)
                .block();// Blokujemy i czekamy na odpowiedź serwera

        return cleanMimeResponse(response);
    }

    private SOAPDataRoot parseEncodedResponse(SOAPDataResponse response) throws SOAPProtocolException {
        try {
            // Pobierz zakodowany XML z odpowiedzi
            String encodedXml = response.getBody().getSearchData().getResult();

            // Usuń nadmiarowe znaki (opcjonalne), jeśli XML zawiera niepotrzebne białe znaki
            String decodedXml = org.jsoup.parser.Parser.unescapeEntities(encodedXml, false);

            // Parsowanie zakodowanego XML na obiekt Root
            JAXBContext context = JAXBContext.newInstance(SOAPDataRoot.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (SOAPDataRoot) unmarshaller.unmarshal(new StringReader(decodedXml));
        } catch (JAXBException e) {
            throw new SOAPProtocolException("Error decoding or parsing inner XML", e);
        }
    }

    private void updateApiKey(String apiKey) {
        if (isNotNullOrEmpty(apiKey)) {
            API_KEY = apiKey;
        }
    }

    private boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String cleanMimeResponse(String response) {
        // Znajdź początek XML-a
        int startIndex = response.indexOf("<s:Envelope");
        if (startIndex == -1) {
            return response; // Jeśli XML nie został znaleziony, zwracamy oryginał (lub rzucamy wyjątek)
        }
        // Znajdź koniec XML-a
        int endIndex = response.indexOf("</s:Envelope>") + "</s:Envelope>".length();
        if (endIndex == -1) {
            return response; // Jeśli nie ma znacznika zamykającego, zwracamy oryginał (lub rzucamy wyjątek)
        }
        // Wytnij zawartość od <s:Envelope> do </s:Envelope>
        return response.substring(startIndex, endIndex);
    }
}

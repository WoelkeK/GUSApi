package pl.woelke.gus_api.soap.util;

import jakarta.xml.soap.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SoapEnvelopeBuilder {

    private static final String NAMESPACE = "http://CIS/BIR/PUBL/2014/07";
    private static final String NAMESPACE_STATUS = "http://CIS/BIR//2014/07";
    private static final String SOAP_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
    private static final String WSA_NAMESPACE = "http://www.w3.org/2005/08/addressing";
    private static final String SOAP_URL = "https://wyszukiwarkaregontest.stat.gov.pl/wsBIR/UslugaBIRzewnPubl.svc";

    public String createLoginEnvelope(String apiKey) {
        try {
            // Przygotuj pełną wiadomość SOAP
            SOAPMessage soapMessage = createAndSetupEnvelope("http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/Zaloguj", false);
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();


            SOAPBody body = envelope.getBody();
            body.addBodyElement(envelope.createName("Zaloguj", "", NAMESPACE))
                    .addChildElement("pKluczUzytkownika", "", NAMESPACE)
                    .addTextNode(apiKey);

            // Loguj wynik i zwróć wiadomość
            return messageToString(soapMessage);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating login SOAP Envelope", ex);
        }
    }

    public String createDataRequestEnvelopeWithTaxNo(String taxNo) {
        try {
            // Przygotuj pełną wiadomość SOAP z przestrzenią nazw
            SOAPMessage soapMessage = createAndSetupEnvelope("http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/DaneSzukajPodmioty", true);
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            SOAPBody body = envelope.getBody();
            SOAPBodyElement searchElement = body.addBodyElement(envelope.createName("DaneSzukajPodmioty", "ns", NAMESPACE));
            SOAPElement parametersElement = searchElement.addChildElement("pParametryWyszukiwania", "ns");

            // Dodanie parametrów wyszukiwania
            addOptionalElement(parametersElement, "Krs", "?");
            addOptionalElement(parametersElement, "Krsy", "?");
            addOptionalElement(parametersElement, "Nip", taxNo);
            addOptionalElement(parametersElement, "Nipy", "?");
            addOptionalElement(parametersElement, "Regon", "?");
            addOptionalElement(parametersElement, "Regony14zn", "?");
            addOptionalElement(parametersElement, "Regony9zn", "?");

            // Loguj wynik i zwróć wiadomość
            return messageToString(soapMessage);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating Data Request SOAP Envelope", ex);
        }
    }

    public String createLogoutEnvelope(String sessionId) {
        try {
            // Przygotuj pełną wiadomość SOAP
            SOAPMessage soapMessage = createAndSetupEnvelope("http://CIS/BIR/PUBL/2014/07/IUslugaBIRzewnPubl/Wyloguj", false);

            // Wylogowanie (ciało może być puste)
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            envelope.getBody().addBodyElement(envelope.createName("Wyloguj", "ns", NAMESPACE))
                    .addChildElement("pIdentyfikatorSesji", "ns", NAMESPACE)
                    .addTextNode(sessionId);;

            // Loguj wynik i zwróć wiadomość
            return messageToString(soapMessage);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating logout SOAP Envelope", ex);
        }
    }

    public String createStatusEnvelope() {
        try {
            // Przygotuj pełną wiadomość SOAP
            SOAPMessage soapMessage = createAndSetupEnvelope("http://CIS/BIR/2014/07/IUslugaBIR/GetValue", false);


            // Tworzenie ciała żądania SOAP
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            SOAPBody body = envelope.getBody();
            SOAPBodyElement searchElement = body.addBodyElement(envelope.createName("GetValue", "ns", NAMESPACE_STATUS));
            SOAPElement parametersElement = searchElement.addChildElement("pNazwaParametru", "ns").addTextNode("StatusSesji");

            // Loguj wynik i zwróć wiadomość
            return messageToString(soapMessage);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating logout SOAP Envelope", ex);
        }
    }


    private SOAPMessage createAndSetupEnvelope(String actionUrl, boolean addNamespace) throws Exception {
        SOAPMessage soapMessage = createBaseEnvelope(); // Tworzenie bazowego envelope
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();

        if (addNamespace) {
            envelope.addNamespaceDeclaration("dat", "http://CIS/BIR/PUBL/2014/07/DataContract");
        }

        addHeader(envelope, actionUrl); // Dodanie wspólnego nagłówka
        return soapMessage; // Zwracaj pełny obiekt SOAPMessage
    }



    // Pomocnicza metoda do dodawania opcjonalnych elementów
    private void addOptionalElement(SOAPElement parent, String name, String value) throws SOAPException {
        parent.addChildElement(name, "dat").addTextNode(value);
    }

    private SOAPMessage createBaseEnvelope() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("soap", SOAP_NAMESPACE); // Ustaw odpowiednie namespace SOAP 1.2
        envelope.addNamespaceDeclaration("ns", NAMESPACE);       // Namespace dla elementów w body
        envelope.addNamespaceDeclaration("wsa", WSA_NAMESPACE);  // Namespace dla WS-Addressing

        return soapMessage;
    }

    private void addHeader(SOAPEnvelope envelope, String actionUrl) throws SOAPException {
        SOAPHeader header = envelope.getHeader();
        if (header == null) {
            header = envelope.addHeader();
        }

        // Tworzenie elementu Action (nagłówek WS-Addressing)
        SOAPHeaderElement actionElement = header.addHeaderElement(envelope.createName("Action", "wsa", WSA_NAMESPACE));
        actionElement.addTextNode(actionUrl);

        // Dodanie elementu To (kierowanie do konkretnego adresata)
        SOAPHeaderElement toElement = header.addHeaderElement(envelope.createName("To", "wsa", WSA_NAMESPACE));
        toElement.addTextNode(SOAP_URL);
    }

    private String messageToString(SOAPMessage message) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);

    }
}

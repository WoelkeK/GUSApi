package pl.woelke.gus_api.soap.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import pl.woelke.gus_api.soap.response.model.SOAPEnvelopeBase;


// Klasa dla odpowiedzi SOAP z odpowiedzią WylogujResponse
@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOAPLogoutResponse extends SOAPEnvelopeBase {

    // Element <s:Body>, w którym znajduje się WylogujResponse
    @XmlElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
    private Body body;

    // Getter i setter dla Body
    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    // Wewnętrzna klasa reprezentująca Body
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {

        @XmlElement(name = "WylogujResponse", namespace = "http://CIS/BIR/PUBL/2014/07")
        private LogoutResponse logoutResponse;

        public LogoutResponse getLogoutResponse() {
            return logoutResponse;
        }

        public void setLogoutResponse(LogoutResponse logoutResponse) {
            this.logoutResponse = logoutResponse;
        }
    }

    // Klasa wewnętrzna dla elementu <WylogujResponse>
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LogoutResponse {

        @XmlElement(name = "WylogujResult", namespace = "http://CIS/BIR/PUBL/2014/07")
        private boolean logoutResult; // Zwraca true/false jako boolean

        public boolean isLogoutResult() {
            return logoutResult;
        }

        public void setLogoutResult(boolean logoutResult) {
            this.logoutResult = logoutResult;
        }
    }
}

package pl.woelke.gus_api.soap.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SOAPAbstractResponse {


    // Element ciała (Body), specyficzny dla odpowiedzi SOAPLoginResponse
    @XmlElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
    private SOAPLoginResponse.Body body;

    public SOAPLoginResponse.Body getBody() {
        return body;
    }

    public void setBody(SOAPLoginResponse.Body body) {
        this.body = body;
    }

    // Wewnętrzna klasa reprezentująca Body
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "ZalogujResponse", namespace = "http://CIS/BIR/PUBL/2014/07")
        private SOAPLoginResponse.LoginResponse loginResponse;

        public SOAPLoginResponse.LoginResponse getLoginResponse() {
            return loginResponse;
        }

        public void setLoginResponse(SOAPLoginResponse.LoginResponse loginResponse) {
            this.loginResponse = loginResponse;
        }
    }

    // Wewnętrzna klasa dla ZalogujResponse
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LoginResponse {
        @XmlElement(name = "ZalogujResult", namespace = "http://CIS/BIR/PUBL/2014/07")
        private String LoginResult;

        public String getLoginResult() {
            return LoginResult;
        }

        public void setLoginResult(String loginResult) {
            this.LoginResult = loginResult;
        }
    }
}

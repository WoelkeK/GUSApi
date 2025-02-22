package pl.woelke.gus_api.soap.response.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SOAPEnvelopeBase {

    // Nagłówek (Header) wspólny dla wszystkich odpowiedzi SOAP
    @XmlElement(name = "Header", namespace = "http://www.w3.org/2003/05/soap-envelope")
    private Header header;

    // Getter i setter dla Header
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    // Wewnętrzna klasa dla Header
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        @XmlElement(name = "Action", namespace = "http://www.w3.org/2005/08/addressing")
        private String action;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

}

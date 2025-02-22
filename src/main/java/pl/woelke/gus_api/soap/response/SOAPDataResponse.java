package pl.woelke.gus_api.soap.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import pl.woelke.gus_api.soap.response.model.SOAPEnvelopeBase;


@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOAPDataResponse extends SOAPEnvelopeBase {


    @XmlElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    // Wewnętrzna klasa reprezentująca Body
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {

        @XmlElement(name = "DaneSzukajPodmiotyResponse", namespace = "http://CIS/BIR/PUBL/2014/07")
        private SearchData searchData;

        public SearchData getSearchData() {
            return searchData;
        }

        public void setSearchData(SearchData searchData) {
            this.searchData = searchData;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SearchData {

        @XmlElement(name = "DaneSzukajPodmiotyResult", namespace = "http://CIS/BIR/PUBL/2014/07")
        private String result; // Zwraca true/false jako boolean

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}

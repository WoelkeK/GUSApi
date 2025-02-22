package pl.woelke.gus_api.soap.response.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class SOAPDataRoot {

    @XmlElement(name = "dane")
    private SOAPCompany data;

    public SOAPCompany getData() {
        return data;
    }

    public void setData(SOAPCompany data) {
        this.data = data;
    }
}

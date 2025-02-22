package pl.woelke.gus_api.soap.response.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Builder;

@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
public class SOAPCompany {

    @XmlElement(name = "Regon")
    private String registry;

    @XmlElement(name = "Nip")
    private String taxId;

    @XmlElement(name = "StatusNip")
    private String statusTax;

    @XmlElement(name = "Nazwa")
    private String name;

    @XmlElement(name = "Wojewodztwo")
    private String voivodeship;

    @XmlElement(name = "Powiat")
    private String county;

    @XmlElement(name = "Gmina")
    private String district;

    @XmlElement(name = "Miejscowosc")
    private String city;

    @XmlElement(name = "KodPocztowy")
    private String postCode;

    @XmlElement(name = "NrNieruchomosci")
    private String buildingNo;

    @XmlElement(name = "NrLokalu")
    private String officeNo;

    @XmlElement(name = "Typ")
    private String type;

    @XmlElement(name = "SilosID")
    private String silosID;

    @XmlElement(name = "DataZakonczeniaDzialalnosci")
    private String closingDate;

    @XmlElement(name = "MiejscowoscPoczty")
    private String postArea;


    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getStatusTax() {
        return statusTax;
    }

    public void setStatusTax(String statusTax) {
        this.statusTax = statusTax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getOfficeNo() {
        return officeNo;
    }

    public void setOfficeNo(String officeNo) {
        this.officeNo = officeNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSilosID() {
        return silosID;
    }

    public void setSilosID(String silosID) {
        this.silosID = silosID;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getPostArea() {
        return postArea;
    }

    public void setPostArea(String postArea) {
        this.postArea = postArea;
    }
}



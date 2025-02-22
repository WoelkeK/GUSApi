package pl.woelke.gus_api.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class DataResponse {

    private String registry;
    private String tax;
    private String statusTax;
    private String name;
    private String voivodeship;
    private String zone;
    private String district;
    private String city;
    private String postCode;
    private String buildingNo;
    private String officeNo;
    private String type;
    private String closingDate;
    private String post;

}

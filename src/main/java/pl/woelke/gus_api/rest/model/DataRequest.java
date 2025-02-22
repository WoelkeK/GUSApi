package pl.woelke.gus_api.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataRequest {
    private String registryNo;
    private String taxNo;
    private String name;
}

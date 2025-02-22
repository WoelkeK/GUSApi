package pl.woelke.gus_api.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class StatusResponse {
    public String status;
}

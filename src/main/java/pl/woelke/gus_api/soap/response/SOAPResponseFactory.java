package pl.woelke.gus_api.soap.response;


import pl.woelke.gus_api.soap.response.model.SOAPEnvelopeBase;

public class SOAPResponseFactory {

    public static SOAPLoginResponse createLoginResponse(SOAPEnvelopeBase baseResponse) {
        if (baseResponse instanceof SOAPLoginResponse) {
            return (SOAPLoginResponse) baseResponse;
        }
        throw new IllegalArgumentException("Invalid response type: " + baseResponse.getClass().getName());
    }

    public static SOAPLogoutResponse createLogoutResponse(SOAPEnvelopeBase baseResponse) {
        if (baseResponse instanceof SOAPLogoutResponse) {
            return (SOAPLogoutResponse) baseResponse;
        }
        throw new IllegalArgumentException("Invalid response type: " + baseResponse.getClass().getName());
    }

    public static SOAPDataResponse createDataResponse(SOAPEnvelopeBase baseResponse) {
        if (baseResponse instanceof SOAPDataResponse) {
            return (SOAPDataResponse) baseResponse;
        }
        throw new IllegalArgumentException("Invalid response type: " + baseResponse.getClass().getName());
    }

}

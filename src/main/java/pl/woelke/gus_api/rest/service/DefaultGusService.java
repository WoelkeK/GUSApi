package pl.woelke.gus_api.rest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.woelke.gus_api.rest.exception.SOAPProtocolException;
import pl.woelke.gus_api.rest.model.DataResponse;
import pl.woelke.gus_api.rest.model.LoginRequest;
import pl.woelke.gus_api.rest.model.LoginResponse;
import pl.woelke.gus_api.rest.model.LogoutResponse;
import pl.woelke.gus_api.rest.model.StatusResponse;
import pl.woelke.gus_api.rest.util.GusSessionStorage;
import pl.woelke.gus_api.soap.response.model.SOAPDataRoot;
import pl.woelke.gus_api.soap.service.GusSoapService;


@Service
@Slf4j
public class DefaultGusService implements GusService {

    private final GusSessionStorage gusSessionStorage;
    private final GusSoapService gusSoapService;

    public DefaultGusService(GusSessionStorage gusSessionStorage, GusSoapService gusSoapService) {
        this.gusSessionStorage = gusSessionStorage;
        this.gusSoapService = gusSoapService;
    }

    @Override
    public LoginResponse login(LoginRequest request) throws SOAPProtocolException {
        String sessionId = gusSoapService.login(request.getApi_key());

        gusSessionStorage.setSessionId(sessionId, 60);
        log.debug("Loggin in to SOAP service...");
        return LoginResponse.builder()
                .token(sessionId)
                .build();
    }

    @Override
    public LogoutResponse logout() throws SOAPProtocolException {
        String storedSessionId = gusSessionStorage.getSessionId();
        if (storedSessionId == null) {
            return LogoutResponse.builder().message("Brak informacji o sesji lub sesja nie istnieje.").build();
        }
        String logoutResponse = gusSoapService.logout(storedSessionId);
        log.debug("Logging out sessionID {}", storedSessionId);

        clearSessionId();
        log.debug("Logging out from SOAP service...");
        return LogoutResponse.builder().message("Wylogowano z sesji: " + storedSessionId).build();
    }

//    ----------------------------------------------------------------------------------------------------------------------

    @Override
    public DataResponse getData(String identifier) throws SOAPProtocolException {
        if (checkIsActiveSessionId()) {
            String sessionId = gusSessionStorage.getSessionId();
            SOAPDataRoot companyData = gusSoapService.getData(sessionId, identifier);

            // Walidacja danych
            if (companyData == null || companyData.getData() == null) {
                throw new SOAPProtocolException("Dane zwrócone z gusSoapService są puste.");
            }

            return DataResponse.builder()
                    .registry(companyData.getData().getRegistry())
                    .tax(companyData.getData().getTaxId())
                    .statusTax(companyData.getData().getStatusTax())
                    .name(companyData.getData().getName())
                    .voivodeship(companyData.getData().getVoivodeship())
                    .zone(companyData.getData().getCounty())
                    .district(companyData.getData().getDistrict())
                    .city(companyData.getData().getCity())
                    .postCode(companyData.getData().getPostCode())
                    .buildingNo(companyData.getData().getBuildingNo())
                    .officeNo(companyData.getData().getOfficeNo())
                    .type(companyData.getData().getType())
                    .closingDate(companyData.getData().getClosingDate())
                    .post(companyData.getData().getPostArea())
                    .build();
        }
        return null;
    }

    @Override
    public StatusResponse getStatus() throws SOAPProtocolException {
        if (checkIsActiveSessionId()) {
            String serviceValue = gusSoapService.getStatus(gusSessionStorage.getSessionId());
            return StatusResponse.builder()
                    .status(serviceValue)
                    .build();
        }
        return StatusResponse.builder()
                .status("Brak informacji o sesji lub sesja nie istnieje.")
                .build();
    }

    private boolean checkIsActiveSessionId() {
        String sessionId = gusSessionStorage.getSessionId();
        if (sessionId == null || gusSessionStorage.isSessionExpired()) {
            log.info("No session Id");
            return false;
        }
        return true;
    }

    private void clearSessionId() {
        gusSessionStorage.clearSession();
        if (gusSessionStorage.getSessionId() == null) {
            log.info("Logged out successfully.");
        } else {
            log.info("There is an error while logging out.");
        }
    }
}

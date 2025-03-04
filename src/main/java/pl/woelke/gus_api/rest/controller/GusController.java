package pl.woelke.gus_api.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.woelke.gus_api.rest.exception.SOAPProtocolException;
import pl.woelke.gus_api.rest.model.DataResponse;
import pl.woelke.gus_api.rest.model.LoginRequest;
import pl.woelke.gus_api.rest.model.LoginResponse;
import pl.woelke.gus_api.rest.model.LogoutResponse;
import pl.woelke.gus_api.rest.service.GusService;


@RestController
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "GUS Controller", description = "Endpointy do integracji z systemem GUS")
public class GusController {

    private final GusService gusService;

    @Autowired
    public GusController(GusService gusService) {
        this.gusService = gusService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Zaloguj do GUS",
            description = "Ten endpoint służy do logowania w systemie GUS na podstawie identyfikatora"
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response;
        try {
            response = gusService.login(request);
        } catch (SOAPProtocolException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/data")
    @Operation(
            summary = "Pobierz dane z GUS",
            description = "Ten endpoint służy do pobierania danych z systemu GUS przy aktywnej sesji"
    )
    public ResponseEntity<DataResponse> getDataFromSoap(@RequestParam(value = "nip", required = true) String identifier) {
        DataResponse response;
        try {
            response = gusService.getData(identifier);
        } catch (SOAPProtocolException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (response == null) {
            log.debug("Response from SOAP service: {}", response);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @Operation(
            summary = "Wyloguj z GUS",
            description = "Ten endpoint służy do wylogowania z systemu GUS i zamyka otwartą sesje"
    )
    public ResponseEntity<LogoutResponse> logout() {
        try {
            return new ResponseEntity<>(gusService.logout(), HttpStatus.OK);
        } catch (SOAPProtocolException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}

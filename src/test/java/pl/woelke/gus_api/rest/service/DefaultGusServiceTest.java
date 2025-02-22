package pl.woelke.gus_api.rest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.woelke.gus_api.rest.exception.SOAPProtocolException;
import pl.woelke.gus_api.rest.model.DataResponse;
import pl.woelke.gus_api.rest.model.LoginRequest;
import pl.woelke.gus_api.rest.model.LoginResponse;
import pl.woelke.gus_api.rest.model.LogoutResponse;
import pl.woelke.gus_api.rest.util.GusSessionStorage;
import pl.woelke.gus_api.soap.response.model.SOAPCompany;
import pl.woelke.gus_api.soap.response.model.SOAPDataRoot;
import pl.woelke.gus_api.soap.service.GusSoapService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class DefaultGusServiceTest {

    private DefaultGusService defaultGusService;
    private GusSessionStorage gusSessionStorage;
    private GusSoapService gusSoapService;


    @BeforeEach
    void setUp() {

        gusSessionStorage = mock(GusSessionStorage.class);
        gusSoapService = mock(GusSoapService.class);
        defaultGusService = new DefaultGusService(gusSessionStorage, gusSoapService);

    }

    @Test
    void login_ShouldReturnLoginResponse_WhenValidRequest() throws SOAPProtocolException {

        // given
        String apiKey = "test-api-key";
        String sessionId = "test-session-id";
        LoginRequest request = new LoginRequest();
        request.setApi_key(apiKey);

        when(gusSoapService.login(apiKey)).thenReturn(sessionId);
        // when
        LoginResponse response = defaultGusService.login(request);

        // then
        assertNotNull(response);
        assertEquals(sessionId, response.getToken(), "The token in the response should match the sessionId returned by gusSoapService");
        verify(gusSoapService, times(1)).login(apiKey);
        verify(gusSessionStorage, times(1)).setSessionId(sessionId, 60);
    }

    @Test
    void login_ShouldThrowSOAPProtocolException_WhenSoapServiceFails() throws SOAPProtocolException {

        // given
        String apiKey = "test-api-key";
        LoginRequest request = new LoginRequest();
        request.setApi_key(apiKey);

        when(gusSoapService.login(apiKey)).thenThrow(new SOAPProtocolException("SOAP Error"));

        // when
        SOAPProtocolException exception = assertThrows(SOAPProtocolException.class, () -> {
            defaultGusService.login(request);
        });

        // then
        assertEquals("SOAP Error", exception.getMessage(), "Exception message should match the error thrown by gusSoapService");
        verify(gusSoapService, times(1)).login(apiKey);
        verifyNoInteractions(gusSessionStorage);
    }


    @Test
    void logout_ShouldReturnMessage_WhenSessionIdIsNull() throws SOAPProtocolException {
        // Arrange
        when(gusSessionStorage.getSessionId()).thenReturn(null);

        // Act
        LogoutResponse response = defaultGusService.logout();

        // Assert
        assertNotNull(response);
        assertEquals("Brak informacji o sesji lub sesja nie istnieje.", response.getMessage());
        verify(gusSoapService, never()).logout(anyString()); // Metoda logout z gusSoapService nie powinna być wywoływana
    }


    @Test
    void logout_ShouldLogoutSuccessfully_WhenSessionIdExists() throws SOAPProtocolException {
        // Arrange
        String sessionId = "test-session-id";
        when(gusSessionStorage.getSessionId()).thenReturn(sessionId);
        when(gusSoapService.logout(sessionId)).thenReturn("logout-success");

        // Act
        LogoutResponse response = defaultGusService.logout();

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Wylogowano z sesji: " + sessionId));
        assertTrue(response.getMessage().contains(sessionId));
        verify(gusSoapService, times(1)).logout(sessionId);
        verify(gusSessionStorage, times(2)).getSessionId();
    }

    @Test
    void logout_ShouldThrowSOAPProtocolException_WhenLogoutFails() throws SOAPProtocolException {
        // Arrange
        String sessionId = "test-session-id";
        when(gusSessionStorage.getSessionId()).thenReturn(sessionId);
        doThrow(new SOAPProtocolException("SOAP logout error")).when(gusSoapService).logout(sessionId);

        // Act & Assert
        SOAPProtocolException exception = assertThrows(SOAPProtocolException.class, () -> {
            defaultGusService.logout();
        });

        assertEquals("SOAP logout error", exception.getMessage());
        verify(gusSoapService, times(1)).logout(sessionId);
        // Ensure cleanup is not called in case of failure
        verify(gusSessionStorage, never()).clearSession();
    }


    @Test
    void logout_ShouldClearSession_WhenLogoutIsSuccessful() throws SOAPProtocolException {
        // Arrange: ustawiamy symulację dla istniejącej sesji i poprawnego wylogowania
        String sessionId = "test-session-id";
        when(gusSessionStorage.getSessionId()).thenReturn(sessionId); // Mock: getSessionId zwraca "sessionId"
        when(gusSoapService.logout(sessionId)).thenReturn("logout-success"); // Mock: logout zwraca sukces

        // Act: wykonujemy metodę logout()
        LogoutResponse response = defaultGusService.logout();

        // Assert: weryfikujemy wyniki
        assertNotNull(response); // Response nie może być null
        assertTrue(response.getMessage().contains("Wylogowano z sesji: " + sessionId)); // Oczekiwany komunikat w odpowiedzi
        verify(gusSoapService, times(1)).logout(sessionId); // Weryfikujemy, że metoda logout() z gusSoapService została wywołana raz
        verify(gusSessionStorage, times(1)).clearSession(); // Upewniamy się, że sesja została wyczyszczona (clearSessionId() wywołano raz)
    }


//    @Test
//    void getData_ShouldReturnDataResponse_WhenSessionIsActiveAndDataIsValid() throws SOAPProtocolException {
//        // Arrange
//        String identifier = "testIdentifier";
//        String sessionId = "testSessionId";
//
//        // Mock danych sesji
//        when(gusSessionStorage.getSessionId()).thenReturn(sessionId);
//        when(gusSessionStorage.isSessionExpired()).thenReturn(false);
//
//        // Mock danych SOAP
//        SOAPDataRoot soapDataRoot = new SOAPDataRoot();
//        SOAPCompany soapCompanyData = SOAPCompany.builder().build();
//        soapCompanyData.setRegistry("TestRegistry");
//        soapCompanyData.setTaxId("123456789");
//        soapDataRoot.setData(soapCompanyData); // Dane są ustawione
//
//        when(gusSoapService.getData(sessionId, identifier)).thenReturn(soapDataRoot);
//
//        // Act
//        DataResponse response = defaultGusService.getData(identifier);
//
//        // Assert
//        assertNotNull(response); // Sprawdzamy, że wynik nie jest null
//        assertEquals("TestRegistry", response.getRegistry());
//        assertEquals("123456789", response.getTax());
//
//        verify(gusSessionStorage, times(2)).getSessionId();
//        verify(gusSoapService, times(1)).getData(sessionId, identifier);
//    }



    @Test
    void getData_ShouldReturnNull_WhenSessionIsInactive() throws SOAPProtocolException {
        // Arrange
        String identifier = "test-identifier";

        // Mock nieaktywnej sesji
        when(gusSessionStorage.getSessionId()).thenReturn(null); // Brak identyfikatora sesji
        when(gusSessionStorage.isSessionExpired()).thenReturn(true); // Sesja wygasła

        // Act
        DataResponse response = defaultGusService.getData(identifier);

        // Assert
        assertNull(response); // Oczekujemy, że odpowiedź będzie null
        verify(gusSessionStorage, times(1)).getSessionId();
        verify(gusSoapService, never()).getData(anyString(), anyString());
    }

    @Test
    void getData_ShouldThrowException_WhenSoapResponseIsNull() throws SOAPProtocolException {
        // Arrange
        String identifier = "testIdentifier";
        String sessionId = "testSessionId";

        // Mock danych sesji
        when(gusSessionStorage.getSessionId()).thenReturn(sessionId);
        // Symulacja, że `gusSoapService.getData` zwraca `SOAPDataRoot` z `null` w polu `data`
        SOAPDataRoot soapDataRoot = new SOAPDataRoot();
        soapDataRoot.setData(null); // Pole `data` jest null
        when(gusSoapService.getData(sessionId, identifier)).thenReturn(soapDataRoot);

        // Act & Assert
        SOAPProtocolException exception = assertThrows(SOAPProtocolException.class,
                () -> defaultGusService.getData(identifier));
        assertEquals("Dane zwrócone z gusSoapService są puste.", exception.getMessage());
    }



}
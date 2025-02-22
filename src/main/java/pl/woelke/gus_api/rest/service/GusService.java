package pl.woelke.gus_api.rest.service;


import pl.woelke.gus_api.rest.exception.SOAPProtocolException;
import pl.woelke.gus_api.rest.model.DataResponse;
import pl.woelke.gus_api.rest.model.LoginRequest;
import pl.woelke.gus_api.rest.model.LoginResponse;
import pl.woelke.gus_api.rest.model.LogoutResponse;
import pl.woelke.gus_api.rest.model.StatusResponse;

/**
 *@author Created by Krzysztof Woelke on 16.02.2025
 */
public interface GusService {

    /**
     * Retrieves the current session status.
     *
     * @return a {@link StatusResponse} object containing the status of the current session.
     *         If there is no active session, the status will indicate that no session information exists.
     *
     */
    StatusResponse getStatus() throws SOAPProtocolException;

    /**
     * Ends the current user session by clearing the session ID and logging out from the SOAP service.
     *
     * @return a {@link LogoutResponse} containing a message indicating the result of the logout operation.
     */
    LogoutResponse logout() throws SOAPProtocolException;

    /**
     * Retrieves the data associated with the given identifier.
     *
     * @param identifier the unique identifier (e.g., NIP) used to query for data.
     * @return a {@link DataResponse} object containing detailed information such as registry,
     *         tax number, name, address, and other attributes, or {@code null} if no active
     *         session exists or data cannot be retrieved.
     */
    DataResponse getData(String identifier) throws SOAPProtocolException;

    /**
     * Executes the login process using the provided request containing login details.
     *
     * @param request the login request containing the API key for authentication
     *                in the json format {"apiKey": "XXXXXXXXXXXXXX"}
     * @return the login response containing a token if the login is successful
     */
    LoginResponse login(LoginRequest request) throws SOAPProtocolException;
}

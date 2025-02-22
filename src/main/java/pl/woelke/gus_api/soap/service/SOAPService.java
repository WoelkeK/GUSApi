package pl.woelke.gus_api.soap.service;


import pl.woelke.gus_api.rest.exception.SOAPProtocolException;

public interface SOAPService {

    String login(String apiKey) throws SOAPProtocolException;

    String logout(String sessionId) throws SOAPProtocolException;

    String getStatus(String sessionId) throws SOAPProtocolException;
}

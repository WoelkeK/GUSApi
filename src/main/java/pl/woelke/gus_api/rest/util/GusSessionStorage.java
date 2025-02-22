package pl.woelke.gus_api.rest.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GusSessionStorage {

    private String sessionId;
    private LocalDateTime expirationTime;

    public synchronized String getSessionId() {
        if (sessionId == null || isSessionExpired()) {
            return null;
        }
        return sessionId;
    }

    public synchronized void setSessionId(String sessionId, int validityMinutes) {
        this.sessionId = sessionId;
        this.expirationTime = LocalDateTime.now().plusMinutes(validityMinutes);
    }

    public synchronized boolean isSessionExpired() {
        return expirationTime == null || LocalDateTime.now().isAfter(expirationTime);
    }

    public synchronized void clearSession() {
        this.sessionId = null;
        this.expirationTime = null;
    }
}

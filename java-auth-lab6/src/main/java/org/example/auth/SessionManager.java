package org.example.auth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SessionManager {

    private static final long SESSION_TTL_MILLIS = 30 * 60 * 1000L;

    private static final Map<String, Session> sessions = new HashMap<>();

    public static void createSession(User user, String token) {
        long now = System.currentTimeMillis();
        long expiresAt = now + SESSION_TTL_MILLIS;
        Session session = new Session(token, user, now, expiresAt);
        sessions.put(token, session);
    }

    public static User getUserByToken(String token) {
        if (token == null) {
            return null;
        }

        Session session = sessions.get(token);
        if (session == null) {
            return null;
        }

        long now = System.currentTimeMillis();

        if (!session.isActive() || session.isExpired(now)) {
            sessions.remove(token);
            return null;
        }

        session.touch(now);
        return session.getUser();
    }

    public static void refreshSession(String token) {
        Session session = sessions.get(token);
        if (session == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (session.isExpired(now) || !session.isActive()) {
            sessions.remove(token);
            return;
        }
    }

    public static void endSession(String token) {
        Session session = sessions.get(token);
        if (session != null) {
            session.deactivate();
            sessions.remove(token);
        }
    }

    public static void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Session>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Session> entry = it.next();
            Session s = entry.getValue();
            if (!s.isActive() || s.isExpired(now)) {
                it.remove();
            }
        }
    }

}

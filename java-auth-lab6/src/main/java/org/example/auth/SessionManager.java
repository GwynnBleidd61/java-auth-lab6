package org.example.auth;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, User> sessions = new HashMap<>();

    public static void createSession(User user, String token) {
        sessions.put(token, user);
    }

    public static User getUserByToken(String token) {
        return sessions.get(token);
    }

    public static void removeSession(String token) {
        sessions.remove(token);
    }
}

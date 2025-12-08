package org.example.auth;

public class Session {

    private final String token;
    private final User user;
    private final long createdAt;
    private long lastAccessAt;
    private final long expiresAt;
    private boolean active;

    public Session(String token, User user, long createdAt, long expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastAccessAt = createdAt;
        this.active = true;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastAccessAt() {
        return lastAccessAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public void touch(long now) {
        this.lastAccessAt = now;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isExpired(long now) {
        return now > expiresAt;
    }
}

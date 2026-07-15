package com.books.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtAdapter implements Jwt {

    private final String subject;
    private final Map<String, Object> claims;

    public JwtAdapter(String subject, Map<String, Object> claims) {
        this.subject = subject;
        this.claims = new HashMap<>(claims);
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String claimName, Class<T> type) {
        Object value = claims.get(claimName);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new IllegalArgumentException("Claim '" + claimName + "' is not of type " + type.getName());
    }

    @Override
    public Map<String, Object> getMap() {
        return claims;
    }
}

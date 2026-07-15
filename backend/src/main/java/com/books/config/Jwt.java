package com.books.config;

import java.util.List;
import java.util.Map;

public interface Jwt {

    String getSubject();

    <T> T getClaim(String claimName, Class<T> type);

    @SuppressWarnings("unchecked")
    default <T> T getClaim(String claimName) {
        return (T) getMap().get(claimName);
    }

    Map<String, Object> getMap();
}

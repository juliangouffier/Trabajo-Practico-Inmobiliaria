package com.example.trabajopracticoinmobiliaria.data.remote;

import androidx.annotation.NonNull;

public final class AccessToken {

    private final String value;

    private AccessToken(@NonNull String value) {
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    public static AccessToken fromRawResponse(@NonNull String raw) {
        String t = raw.trim();
        if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
            t = t.substring(1, t.length() - 1).trim();
        }
        return new AccessToken(t);
    }
}

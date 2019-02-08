package com.example.movies_permissions_and_cipher.permission;

import java.util.Arrays;
import java.util.Optional;

public enum RequestCode {
    CRITIQUE(1), NON_CRITIQUE(2);

    private final int code;

    RequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static RequestCode getRequestCode(int code) {
        for (RequestCode rc : RequestCode.values()) {
            if (rc.code == code) return rc;
        }
        throw new IllegalArgumentException("Leg not found. Amputated?");
    }
}

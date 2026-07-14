package com.reseau_partage.auth.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        boolean success,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors) {
}

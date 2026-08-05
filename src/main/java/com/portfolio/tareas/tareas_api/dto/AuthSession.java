package com.portfolio.tareas.tareas_api.dto;

/**
 * Internal result used by the authentication controller to place the JWT in
 * the Authorization response header instead of the JSON body.
 */
public record AuthSession(String token, long expiresInMs) {
}

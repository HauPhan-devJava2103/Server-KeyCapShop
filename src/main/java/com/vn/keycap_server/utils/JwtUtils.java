package com.vn.keycap_server.utils;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUtils {

    /**
     * Extracts a claim safely from the Jwt, handling Number cases properly
     * to avoid ClassCastException (e.g. when a Long is parsed as an Integer).
     *
     * @param jwt       The JWT token
     * @param claimName The name of the claim to extract
     * @return The Long value of the claim
     * @throws IllegalArgumentException if the claim is null or not found
     */
    public static Long getLongClaim(Jwt jwt, String claimName) {
        Object claim = jwt.getClaim(claimName);
        if (claim == null) {
            throw new IllegalArgumentException("JWT claim '" + claimName + "' not found.");
        }
        if (claim instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(claim.toString());
    }

    /**
     * Convenient method to get userId from the Jwt token.
     *
     * @param jwt The JWT token
     * @return The user ID
     */
    public static Long getUserId(Jwt jwt) {
        return getLongClaim(jwt, "userId");
    }
}

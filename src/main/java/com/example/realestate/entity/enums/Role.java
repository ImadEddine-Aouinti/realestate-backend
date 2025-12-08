package com.example.realestate.entity.enums;

/**
 * User roles for authorization.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    public java.util.Collection<String> getAuthorities() {
        return java.util.List.of("ROLE_" + this.name());
    }
}
package com.example.sweater.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	USER, ADMIN;

	public String getAuthority() {
	    // TODO Auto-generated method stub
	    return name(); // Строковое представление значения enum
	}
}

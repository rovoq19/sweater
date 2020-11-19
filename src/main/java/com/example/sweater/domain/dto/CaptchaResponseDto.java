package com.example.sweater.domain.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)//Игнорировать все неизвестные параметры
public class CaptchaResponseDto {
	private boolean success;
	@JsonAlias("error-codes")//Преобразование для символа дефис
	private Set<String> errorsCodes;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Set<String> getErrorsCodes() {
		return errorsCodes;
	}
	public void setErrorsCodes(Set<String> errorsCodes) {
		this.errorsCodes = errorsCodes;
	}
}

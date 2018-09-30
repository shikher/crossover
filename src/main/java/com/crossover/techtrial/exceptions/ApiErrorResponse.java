package com.crossover.techtrial.exceptions;

import org.springframework.http.HttpStatus;

public class ApiErrorResponse extends Exception {

	private HttpStatus status;
    private String message;
	
    public ApiErrorResponse() {
    	
    }
    
    public ApiErrorResponse(HttpStatus status, String message) {
		super();
		this.setStatus(status);
		this.setMessage(message);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}

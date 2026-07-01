package com.paygateway.mock.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ApiErrorResponse {
    public Boolean success = false;
    public String errorCode;
    public String message;
    public Map<String, Object> details;
    public List<ValidationError> validationErrors;
    public Instant timestamp = Instant.now();
    public String path;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String errorCode, String message, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public static class ValidationError {
        public String field;
        public String message;

        public ValidationError() {
        }

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}

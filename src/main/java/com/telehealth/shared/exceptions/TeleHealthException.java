package com.telehealth.shared.exceptions;

import java.util.UUID;

// ─── Base Exception ────────────────────────────────────────────────────────────

public sealed class TeleHealthException extends RuntimeException
        permits TeleHealthException.EntityNotFoundException,
                TeleHealthException.BusinessRuleViolationException,
                TeleHealthException.UnauthorizedException,
                TeleHealthException.ConflictException,
                TeleHealthException.ExternalServiceException {

    private final String errorCode;

    protected TeleHealthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected TeleHealthException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }

    // ─── Sub-exceptions ────────────────────────────────────────────────────────

    public static final class EntityNotFoundException extends TeleHealthException {
        public EntityNotFoundException(String entity, UUID id) {
            super(entity + " not found with id: " + id, "ENTITY_NOT_FOUND");
        }
        public EntityNotFoundException(String entity, String identifier) {
            super(entity + " not found: " + identifier, "ENTITY_NOT_FOUND");
        }
    }

    public static final class BusinessRuleViolationException extends TeleHealthException {
        public BusinessRuleViolationException(String rule) {
            super("Business rule violated: " + rule, "BUSINESS_RULE_VIOLATION");
        }
    }

    public static final class UnauthorizedException extends TeleHealthException {
        public UnauthorizedException(String reason) {
            super("Unauthorized: " + reason, "UNAUTHORIZED");
        }
    }

    public static final class ConflictException extends TeleHealthException {
        public ConflictException(String message) {
            super(message, "CONFLICT");
        }
    }

    public static final class ExternalServiceException extends TeleHealthException {
        public ExternalServiceException(String service, String reason) {
            super("External service '" + service + "' failed: " + reason, "EXTERNAL_SERVICE_ERROR");
        }
        public ExternalServiceException(String service, String reason, Throwable cause) {
            super("External service '" + service + "' failed: " + reason, "EXTERNAL_SERVICE_ERROR", cause);
        }
    }
}

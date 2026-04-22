package mg.hei.agrifed.agrifedapi.exception;

import mg.hei.agrifed.agrifedapi.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

/**
 * Global exception handler returning standardized error responses
 * matching the OpenAPI specification.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==================== SQLException ====================

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSqlException(SQLException ex) {
        logger.error("SQL error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .type("InternalServerException")
                .message("Database error: " + ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ==================== BadRequestException ====================

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("BadRequestException")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }

    // ==================== NotFoundException ====================

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        logger.warn("Not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("ResourceNotFoundException")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ==================== ResourceNotFoundException ====================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {} with id {}", ex.getResourceType(), ex.getResourceId());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("ResourceNotFoundException")
                .message(ex.getMessage())
                .resourceType(ex.getResourceType())
                .resourceId(ex.getResourceId())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ==================== UnauthorizedException ====================

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        logger.warn("Unauthorized: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("UnauthorizedException")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ==================== NotAuthorizedException ====================

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorized(NotAuthorizedException ex) {
        logger.warn("Not authorized: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("NotAuthorizedException")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // ==================== BusinessRuleViolationException ====================
 
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleViolationException ex) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("BusinessRuleViolationException")
                .message(ex.getMessage())
                .rule(ex.getRule())
                .details(ex.getDetails())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // ==================== ConflictException ====================

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        logger.warn("Conflict: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .type("ConflictException")
                .message(ex.getMessage())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // ==================== DatabaseException ====================

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DatabaseException ex) {
        logger.error("Database error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .type("InternalServerException")
                .message("Database error occurred")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ==================== Generic Exception (catch-all) ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .type("InternalServerException")
                .message("Unexpected error occurred")
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
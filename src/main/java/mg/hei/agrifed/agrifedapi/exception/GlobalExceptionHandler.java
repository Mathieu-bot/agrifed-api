package mg.hei.agrifed.agrifedapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    private static class ErrorResponse {
        private final String type;
        private final String message;
        private final String rule;
        private final LocalDateTime timestamp = LocalDateTime.now();

        public ErrorResponse(String type, String message) {
            this(type, message, null);
        }

        public ErrorResponse(String type, String message, String rule) {
            this.type = type;
            this.message = message;
            this.rule = rule;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder("Validation errors: ");
        ex.getBindingResult().getFieldErrors().forEach(e ->
                sb.append(e.getField()).append(" - ").append(e.getDefaultMessage()).append("; "));

        return buildResponse("VALIDATION_ERROR", sb.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return buildResponse("BAD_REQUEST", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse("UNAUTHORIZED", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(NotAuthorizedException ex) {
        return buildResponse("FORBIDDEN", ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            ResourceNotFoundException.class, MemberNotFoundException.class,
            CollectivityNotFoundException.class, MandateNotFoundException.class,
            ActivityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return buildResponse("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return buildResponse("CONFLICT", ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleViolationException ex) {
        return new ResponseEntity<>(new ErrorResponse("BUSINESS_RULE_VIOLATION", ex.getMessage(), ex.getRule()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return buildResponse("INTERNAL_SERVER_ERROR",
                "Une erreur interne est survenue. Veuillez contacter l'administrateur.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String type, String message, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(type, message), status);
    }
}
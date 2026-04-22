package mg.hei.agrifed.agrifedapi.exception;

import lombok.Getter;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
    
    private final String rule;
    private final Object details;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.rule = null;
        this.details = null;
    }

    public BusinessRuleViolationException(String message, String rule) {
        super(message);
        this.rule = rule;
        this.details = null;
    }

    public BusinessRuleViolationException(String message, String rule, Object details) {
        super(message);
        this.rule = rule;
        this.details = details;
    }

    public String getRule() {
        return rule;
    }

    public Object getDetails() {
        return details;
    }
}
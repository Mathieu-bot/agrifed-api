package mg.hei.agrifed.agrifedapi.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceType;
    private final Integer resourceId;

    public ResourceNotFoundException(String resourceType, Integer resourceId) {
        super(String.format("Resource of type %s identified by %d not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.resourceId = null;
    }
}
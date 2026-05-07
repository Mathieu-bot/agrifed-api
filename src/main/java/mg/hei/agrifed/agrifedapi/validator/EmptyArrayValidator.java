package mg.hei.agrifed.agrifedapi.validator;

import mg.hei.agrifed.agrifedapi.exception.BadRequestException;

public class EmptyArrayValidator {

    private EmptyArrayValidator() {
    }

    public static void validateNotEmpty(Object[] array, String fieldName) {
        if (array == null || array.length == 0) {
            throw new BadRequestException(fieldName + " cannot be empty");
        }
    }
}
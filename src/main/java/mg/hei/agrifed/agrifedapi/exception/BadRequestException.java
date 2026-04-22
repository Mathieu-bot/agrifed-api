package mg.hei.agrifed.agrifedapi.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException missingRequiredField(String fieldName) {
        return new BadRequestException(
                "The field '" + fieldName + "' is mandatory and cannot be empty"
        );
    }

    public static BadRequestException invalidFormat(String fieldName, String expectedFormat) {
        return new BadRequestException(
                "The field '" + fieldName + "' has an invalid format. Excepted format : " + expectedFormat
        );
    }

    public static BadRequestException invalidEmail(String email) {
        return new BadRequestException(
                "The email '" + email + "' is invalid"
        );
    }

    public static BadRequestException invalidPhoneNumber(String phone) {
        return new BadRequestException(
                "The phone number '" + phone + "' is invalid. Excepted format should contains 10 numbers"
        );
    }

    public static BadRequestException invalidDate(String fieldName, String reason) {
        return new BadRequestException(
                "The date in the field '" + fieldName + "' is invalid : " + reason
        );
    }
}

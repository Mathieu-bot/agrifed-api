package mg.hei.agrifed.agrifedapi.util;

public class EnumConverter {

    private EnumConverter() {
    }

    public static <T extends Enum<T>> T fromDb(String dbValue, Class<T> enumClass) {
        if (dbValue == null) {
            return null;
        }
        return Enum.valueOf(enumClass, dbValue.toUpperCase());
    }

    public static String toDb(Enum<?> enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.name().toLowerCase();
    }
}
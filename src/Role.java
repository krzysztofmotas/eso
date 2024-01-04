public enum Role {
    STUDENT("Student"),
    TEACHER("Nauczyciel"),
    ADMIN("Administrator");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role fromInt(int i) {
        return switch (i) {
            case 0 -> STUDENT;
            case 1 -> TEACHER;
            case 2 -> ADMIN;
            default -> null;
        };
    }
}
public enum Role {
    STUDENT(0, "Student"),
    TEACHER(1, "Nauczyciel");

    private final String name;
    private final int id;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Role fromInt(int i) {
        return switch (i) {
            case 0 -> STUDENT;
            case 1 -> TEACHER;
            default -> null;
        };
    }
}
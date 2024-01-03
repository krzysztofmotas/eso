import java.util.regex.Pattern;

public class Utilities {
    // https://regexr.com/3e48o
    public static boolean emailPatternMatches(String emailAddress) {
        if (emailAddress.length() > 100) {
            return false;
        }

        return Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").
                matcher(emailAddress).
                matches();
    }

    // https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
    public static boolean passwordPatternMatches(String password) {
        if (password.length() > 100) {
            return false;
        }

        return Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$").
                matcher(password).
                matches();
    }
}
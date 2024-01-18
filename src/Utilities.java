import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
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

    public static void setLabelIconToProjectLogo(JLabel label, float scale) {
        FlatSVGIcon flatSVGIcon = new FlatSVGIcon("img/logo.svg", scale);
        label.setIcon(flatSVGIcon);
    }

    public static void setIconToProjectIcon(JFrame frame) {
        try {
            frame.setIconImage(ImageIO.read(new File("src/img/icon.png")));
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Wystąpił błąd przy wczytywaniu icon.png.");
        }
    }
}
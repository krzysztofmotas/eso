import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
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

    public static void centerTextInColumns(JTable table) {
        TableColumnModel columns = table.getColumnModel();
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < columns.getColumnCount(); i++) {
            columns.getColumn(i).setCellRenderer(renderer);
        }
    }

    // https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
    public static void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            // Account for header size
            double width = table.getTableHeader().getHeaderRect(column).getWidth();
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }

            columnModel.getColumn(column).setPreferredWidth((int) width);
        }
    }

    // https://stackoverflow.com/questions/144892/how-to-center-a-window-in-java
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
}
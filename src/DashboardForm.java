import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class DashboardForm extends JFrame {
    private JPanel mainPanel, addGradesPanel, gradesPanel, finalGradesPanel, gradesReportPanel, statsPanel;
    private JTabbedPane tabbedPane;
    private JLabel nameLabel, emailLabel, roleNameLabel, logoLabel;
    private JTable gradesTable, studentsTable, finalGradesTable, reportTable, statsTable;
    private JScrollPane addGradesScrollPane;
    private JButton confirmAddGradesButton, logoutButton, reportButton;
    private JComboBox<String> subjectsComboBox, gradesTypeComboBox;
    private JTextField reportNameField, reportSurnameField;
    private final User user;
    private final HashMap<String, Integer> subjectsHashMap = new HashMap<>();

    public DashboardForm(User user) {
        this.user = user;

        setTitle("Panel zarządzania");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(700, 450);
        setResizable(false);

        Utilities.setLabelIconToProjectLogo(logoLabel, 0.15f);
        Utilities.setIconToProjectIcon(this);

        nameLabel.setText(user.getName() + " " + user.getSurname());
        emailLabel.setText(user.getEmailAddress());
        roleNameLabel.setText(user.getRole().getName());

        // zmiana koloru ikon .svg na kolor GUI
        Color c = UIManager.getColor("ProgressBar.foreground");
        FlatSVGIcon.ColorFilter.getInstance().add(new Color(28, 39, 76), c);
        FlatSVGIcon.ColorFilter.getInstance().add(new Color(28, 39, 77), c);

        float scale = 0.025f;
        tabbedPane.removeAll();
        switch (user.getRole()) {
            case STUDENT -> {
                tabbedPane.addTab("Moje oceny", gradesPanel);
                tabbedPane.setIconAt(0, new FlatSVGIcon("img/crown-star.svg", scale));
                updateGradesTable();

                tabbedPane.addTab("Oceny końcowe", finalGradesPanel);
                tabbedPane.setIconAt(1, new FlatSVGIcon("img/academic-cap.svg", scale));
                updateFinalGradesTable();
            }
            case TEACHER -> {
                tabbedPane.addTab("Wstawianie ocen", addGradesPanel);
                tabbedPane.setIconAt(0, new FlatSVGIcon("img/add.svg", scale));
                updateAddGradesPanel();

                tabbedPane.addTab("Raport ocen", gradesReportPanel);
                tabbedPane.setIconAt(1, new FlatSVGIcon("img/list.svg", scale));

                tabbedPane.addTab("Statystyki", statsPanel);
                tabbedPane.setIconAt(2, new FlatSVGIcon("img/pie-chart.svg", scale));
            }
        }

        tabbedPane.addChangeListener(e -> { // wywoływane podczas zmiany panelu
            JPanel panel = (JPanel) tabbedPane.getSelectedComponent();

            if (panel.equals(gradesReportPanel)) { // wyczyszczenie zawartości panelu raportu ocen
                reportNameField.setText("");
                reportSurnameField.setText("");
                reportTable.setModel(new DefaultTableModel());
           } else if (panel.equals(statsPanel)) {
                updateStatsPanel();
            }
        });

        confirmAddGradesButton.addActionListener(e -> {
            String selectedSubject = (String) subjectsComboBox.getSelectedItem();
            if (selectedSubject == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nie wybrano przedmiotu!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String selectedGradeType = (String) gradesTypeComboBox.getSelectedItem();
            if (selectedGradeType == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nie wybrano typu oceny!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // id danego wybranego przedmiotu na podstawie klucza, jakim jest jego nazwa
            int selectedSubjectId = subjectsHashMap.get(selectedSubject);

            // liczba wstawionych ocen
            int gradesCount = 0;

            try {
                Connection connection = Database.getConnection();
                String sql = "INSERT INTO grades (student_id, teacher_id, grade, type, subject_id) VALUES (?, ?, ?, ?, ?)";

                for (int i = 0; i < studentsTable.getRowCount(); i++) {
                    CellEditor cellEditor = studentsTable.getCellEditor(i, 2);
                    Object grade = cellEditor.getCellEditorValue();

                    if (grade == null) {
                        // dla danego studenta nie zmieniono oceny
                        continue;
                    }
                    int id = Integer.parseInt((String) studentsTable.getValueAt(i, 0));

                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, id);
                    preparedStatement.setInt(2, user.getId());
                    preparedStatement.setDouble(3, (Double) grade);
                    preparedStatement.setString(4, selectedGradeType);
                    preparedStatement.setInt(5, selectedSubjectId);

                    preparedStatement.execute();
                    preparedStatement.close();

                    gradesCount++;
                }

                if (gradesCount == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Nie została dodana żadna nowa ocena.",
                            "Ostrzeżenie",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    updateAddGradesPanel();

                    JOptionPane.showMessageDialog(
                         this,
                         "Pomyślnie dodano nowe oceny. Liczba ocen: " + gradesCount,
                         "Informacja",
                         JOptionPane.INFORMATION_MESSAGE
                    );
                }

                connection.close();
            } catch (SQLException exception) {
                showErrorMessageDialog(exception);
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();

            LoginForm loginForm = new LoginForm(false);
            loginForm.setVisible(true);
        });

        reportButton.addActionListener(e -> {
            String name = reportNameField.getText();

            if (name.isBlank()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Nie podano imienia studenta.",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String surname = reportSurnameField.getText();

            if (surname.isBlank()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Nie podano nazwiska studenta.",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            try {
                Connection connection = Database.getConnection();

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, surname FROM accounts WHERE name = ? AND surname = ? AND role = ?");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setInt(3, Role.STUDENT.getId());

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int studentId = resultSet.getInt("id");

                    DefaultTableModel tableModel = new DefaultTableModel(
                            new String[] {
                                    "Przedmiot",
                                    "Oceny",
                                    "Średnia ocen"
                            },
                            0
                    );

                    // zarówno preparedStatement, jak i resultSet z danymi dotyczącymi studenta nie będą już potrzebne
                    preparedStatement.close();
                    preparedStatement = connection.prepareStatement("SELECT * FROM subjects");

                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        PreparedStatement ps = connection.prepareStatement("SELECT grade FROM grades WHERE subject_id = ? AND student_id = ?");
                        ps.setInt(1, resultSet.getInt("id")); // subject_id
                        ps.setInt(2, studentId);

                        String subjectName = resultSet.getString("name");
                        StringBuilder gradesString = new StringBuilder();

                        ResultSet rs = ps.executeQuery();
                        double gradesSum = 0;
                        int gradesCount = 0;

                        while (rs.next()) {
                            double grade = rs.getDouble("grade");
                            gradesSum += grade;

                            if (!gradesString.isEmpty()) {
                                gradesString.append(", ");
                            }

                            gradesString.append(grade);
                            gradesCount++;
                        }

                        if (gradesString.isEmpty()) {
                            gradesString.append("-");
                        }

                        tableModel.addRow(
                                new String[] {
                                        subjectName,
                                        gradesString.toString(),
                                        gradesSum == 0.0 ? "-" : String.format("%.2f", gradesSum / gradesCount)
                                }
                        );

                        ps.close();
                    }

                    reportTable.setModel(tableModel);
                    Utilities.centerTextInColumns(reportTable);
                    Utilities.resizeColumnWidth(reportTable);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Nie znaleziono studenta o podanym imieniu i nazwisku.",
                            "Informacja",
                            JOptionPane.WARNING_MESSAGE
                    );
                }

                preparedStatement.close();
                connection.close();
            } catch (SQLException exception) {
                showErrorMessageDialog(exception);
            }
        });
    }

    private void updateFinalGradesTable() {
        try {
            Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM subjects");

            DefaultTableModel defaultTableModel = new DefaultTableModel(
                    new String[] {
                            "Przedmiot",
                            "Średnia ocen",
                            "Ocena końcowa"
                    },
                    0
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            String sql = """
                SELECT AVG(grade) AS average_grade FROM grades
                JOIN accounts ON accounts.id = grades.student_id
                WHERE grades.subject_id = ? AND accounts.id = ?
            """;

            while (resultSet.next()) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, resultSet.getInt("id"));
                ps.setInt(2, user.getId());

                ResultSet rs = ps.executeQuery();
                double averageGrade = rs.next() ? rs.getDouble("average_grade") : 0.0;

                defaultTableModel.addRow(
                        new String[] {
                                resultSet.getString("name"),
                                averageGrade == 0.0 ? "-" : String.format("%.2f", averageGrade),
                                averageGrade == 0.0 ? "-" : String.valueOf(Math.round(averageGrade))
                        }
                );

                ps.close();
            }
            finalGradesTable.setModel(defaultTableModel);
            Utilities.centerTextInColumns(finalGradesTable);
            Utilities.resizeColumnWidth(finalGradesTable);

            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            showErrorMessageDialog(exception);
        }
    }

    private static final String[] gradeTypesList = {
            "Praca domowa",
            "Wejściówka",
            "Kolokwium",
            "Aktywność",
            "Inne"
    };

    private static final Double[] gradesList = {
            2.0,
            3.0,
            3.5,
            4.0,
            4.5,
            5.0
    };

    private void updateAddGradesPanel() {
        gradesTypeComboBox.removeAllItems();

        for (String type : gradeTypesList) {
            gradesTypeComboBox.addItem(type);
        }

        gradesTypeComboBox.setSelectedIndex(-1);

        try {
            Connection connection = Database.getConnection();
            Statement statement = connection.createStatement();

            ResultSet subjectsResultSet = statement.executeQuery("SELECT * FROM subjects");

            subjectsComboBox.removeAllItems();
            subjectsHashMap.clear();
            while (subjectsResultSet.next()) {
                subjectsHashMap.put(subjectsResultSet.getString("name"), subjectsResultSet.getInt("id"));
                subjectsComboBox.addItem(subjectsResultSet.getString("name"));
            }

            subjectsComboBox.setSelectedIndex(-1);

            DefaultTableModel defaultTableModel = new DefaultTableModel(
                    new String[] {
                            "Nr indeksu",
                            "Imię i nazwisko",
                            "Nowa ocena"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // 2 - nowa ocena
                }
            };

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, surname FROM accounts WHERE role = ?");
            preparedStatement.setInt(1, Role.STUDENT.getId());

            ResultSet studentsResultSet = preparedStatement.executeQuery();
            while (studentsResultSet.next()) {
                defaultTableModel.addRow(
                        new String[] {
                                String.valueOf(studentsResultSet.getInt("id")),
                                studentsResultSet.getString("name") + " " + studentsResultSet.getString("surname")
                        }
                );
            }

            studentsTable = new JTable(defaultTableModel) {
                private final HashMap<Integer, DefaultCellEditor> cellEditors = new HashMap<>();

                @Override
                public TableCellEditor getCellEditor(int row, int column) {
                    if (column == 2) { // 2 - nowa ocena
                        if (cellEditors.containsKey(row)) {
                            return cellEditors.get(row);
                        } else {
                            JComboBox<Double> comboBox = new JComboBox<>(gradesList);
                            comboBox.setSelectedIndex(-1);

                            DefaultCellEditor newCellEditor = new DefaultCellEditor(comboBox);
                            cellEditors.put(row, newCellEditor);

                            return newCellEditor;
                        }
                    }

                    return super.getCellEditor(row, column);
                }
            };

            studentsTable.getTableHeader().setReorderingAllowed(false);

            addGradesScrollPane.add(studentsTable);
            addGradesScrollPane.setViewportView(studentsTable);
            Utilities.centerTextInColumns(studentsTable);
            Utilities.resizeColumnWidth(studentsTable);

            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (SQLException exception) {
            showErrorMessageDialog(exception);
        }
    }

    private void updateGradesTable() {
        try {
            Connection connection = Database.getConnection();

            String sql = """
                    SELECT grades.*, accounts.name, accounts.surname, subjects.name AS subject_name FROM grades
                    JOIN accounts ON accounts.id = grades.teacher_id
                    JOIN subjects ON subjects.id = grades.subject_id
                    WHERE grades.student_id = ?
                    ORDER BY grades.subject_id, grades.date DESC
            """;

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());

            DefaultTableModel defaultTableModel = new DefaultTableModel(
                    new String[] {
                            "Przedmiot",
                            "Nauczyciel",
                            "Data wstawienia",
                            "Typ oceny",
                            "Ocena"
                    },
                    0 // rowCount
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                defaultTableModel.addRow(
                        new String[] {
                                resultSet.getString("subject_name"),
                                resultSet.getString("name") + " " + resultSet.getString("surname"),
                                resultSet.getString("date"),
                                resultSet.getString("type"),
                                resultSet.getString("grade")
                        }
                );
            }
            gradesTable.setModel(defaultTableModel);
            Utilities.centerTextInColumns(gradesTable);
            Utilities.resizeColumnWidth(gradesTable);

            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            showErrorMessageDialog(exception);
        }
    }

    private void updateStatsPanel() {
        try {
            Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM subjects");

            DefaultTableModel tableModel = new DefaultTableModel(
                    new String[] {
                            "Przedmiot",
                            "Najniższa średnia ocen",
                            "Najwyższa średnia ocen",
                            "Średnia ocen",
                            "Mediana ocen"
                    },
                    0
            );

            String meanSql = """
                   SELECT accounts.name, accounts.surname, AVG(grade) as mean FROM accounts
                   JOIN grades ON grades.student_id = accounts.id
                   WHERE grades.subject_id = ?
                   GROUP BY accounts.id
                   ORDER BY mean
            """;

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Vector<String> rowData = new Vector<>();

                int subjectId = resultSet.getInt("id");
                rowData.add(resultSet.getString("name"));

                // szukamy studenta z najniższą średnią ocen z tego przedmiotu
                PreparedStatement ps = connection.prepareStatement(meanSql + " ASC LIMIT 1");
                ps.setInt(1, subjectId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    rowData.add(String.format("%.2f (%s %s)", rs.getDouble("mean"), rs.getString("name"), rs.getString("surname")));

                    // zamykamy ps i szukamy studenta z najwyższą śednią ocen z tego przedmiotu
                    ps.close();
                    ps = connection.prepareStatement(meanSql + " DESC LIMIT 1");
                    ps.setInt(1, subjectId);

                    rs = ps.executeQuery();
                    rs.next();
                    rowData.add(String.format("%.2f (%s %s)", rs.getDouble("mean"), rs.getString("name"), rs.getString("surname")));

                    // średnia ocen z tego przedmiotu
                    ps.close();
                    ps = connection.prepareStatement("""
                        SELECT AVG(grade) AS mean FROM grades
                        JOIN subjects ON subjects.id = grades.subject_id
                        WHERE grades.subject_id = ?
                    """);

                    ps.setInt(1, subjectId);
                    rs = ps.executeQuery();
                    rs.next();
                    rowData.add(String.format("%.2f", rs.getDouble("mean")));

                    // mediana ocen z tego przedmiotu
                    // https://stackoverflow.com/questions/64755680/calculating-a-simple-median-on-a-column-in-mysql
                    ps.close();
                    ps = connection.prepareStatement("""
                        SELECT DISTINCT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY grades.grade) OVER () median FROM grades
                        JOIN subjects ON subjects.id = grades.subject_id
                        WHERE grades.subject_id = ?
                    """);

                    ps.setInt(1, subjectId);
                    rs = ps.executeQuery();
                    rs.next();
                    rowData.add(String.format("%.2f", rs.getDouble("median")));
                    ps.close();
                } else {
                    for (int i = 0; i < 4; i++) { // brak ocen z tego przedmiotu
                        rowData.add("-");
                    }
                }

                tableModel.addRow(rowData);
            }

            statsTable.setModel(tableModel);
            Utilities.centerTextInColumns(statsTable);
            Utilities.resizeColumnWidth(statsTable);

            preparedStatement.close();
            connection.close();
        } catch (SQLException exception) {
            showErrorMessageDialog(exception);
        }
    }

    private void showErrorMessageDialog(Exception exception) {
        exception.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Wystąpił błąd i czynność nie mogła zostać zakończona.",
            "Błąd",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
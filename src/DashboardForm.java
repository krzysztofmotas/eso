import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.sql.*;
import java.util.*;

public class DashboardForm extends JFrame {
    private JPanel mainPanel, addGradesPanel, gradesPanel;
    private JTabbedPane tabbedPane;
    private JLabel nameLabel, emailLabel, roleNameLabel;
    private JTable gradesTable, studentsTable;
    private JScrollPane addGradesScrollPane;
    private JButton confirmAddGradesButton, logoutButton;
    private JComboBox<String> subjectsComboBox, gradesTypeComboBox;
    private final User user;
    private final HashMap<String, Integer> subjectsHashMap = new HashMap<>();

    public DashboardForm(User user) {
        this.user = user;

        setTitle("Elektroniczny system oceniania");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(830, 400);
        setResizable(false);

        nameLabel.setText(user.getName() + " " + user.getSurname());
        emailLabel.setText(user.getEmailAddress());
        roleNameLabel.setText(user.getRole().getName());

        tabbedPane.removeAll();
        switch (user.getRole()) {
            case STUDENT -> {
                tabbedPane.addTab("Moje oceny", gradesPanel);
                updateGradesTable();
            }
            case TEACHER -> {
                tabbedPane.addTab("Wstawianie ocen", addGradesPanel);
                updateAddGradesPanel();
            }

            case ADMIN -> {

            }
        }

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
    }

    private static final String[] gradeTypesList = {
            "Praca domowa",
            "Kolokwium",
            "Projekt",
            "Aktywność",
            "Egzamin",
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
                            "Lp.",
                            "Przedmiot",
                            "Nauczyciel",
                            "Data wstawienia",
                            "Typ oceny",
                            "Ocena"
                    },
                    0 // rowCount
            );

            ResultSet resultSet = preparedStatement.executeQuery();

            int lp = 1;
            while (resultSet.next()) {
                defaultTableModel.addRow(
                        new String[] {
                                String.format("%d.", lp),
                                resultSet.getString("subject_name"),
                                resultSet.getString("name") + " " + resultSet.getString("surname"),
                                resultSet.getString("date"),
                                resultSet.getString("type"),
                                resultSet.getString("grade")
                        }
                );

                lp++;
            }
            gradesTable.setModel(defaultTableModel);

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
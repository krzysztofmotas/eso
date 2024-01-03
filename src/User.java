public class User {
    private int id;
    private String emailAddress, name, surname;

    public User() {

    }

    public User(int id, String emailAddress, String name, String surname) {
        setId(id);
        setEmailAddress(emailAddress);
        setName(name);
        setSurname(surname);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

package tarc.edu.selfcheckoutapp.Model;

public class User {
    String user_name;
    String user_email;
    String password;
    String tscPin;
    String phone;
    Double balance;

    public User() {
    }

    public User(String user_name, String user_email, String password, String tscPin, String phone, Double balance) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.password = password;
        this.tscPin = tscPin;
        this.phone = phone;
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getTscPin() {
        return tscPin;
    }

    public void setTscPin(String tscPin) {
        this.tscPin = tscPin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}

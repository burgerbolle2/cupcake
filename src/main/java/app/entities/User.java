package app.entities;

public class User {
    private int userId;
    private String email;
    private String password;
    private String role;
    private double balance;

    public User(int userId, String email, String password, String role, double balance) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.balance = balance;
    }

    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public double getBalance() { return balance; }

    public void addBalance(double amount) {
        balance += amount;
    }
}


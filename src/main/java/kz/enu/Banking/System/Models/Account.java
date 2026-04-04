package kz.enu.Banking.System.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;

import java.util.Objects;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String username;
    private String password;
    @Email
    @Column(unique = true)
    private String email;
    private String role;
    private double balance;
    private String currency;
    private Long customerId;

    public Account() {
    }

    public Account(Long id, String accountNumber, String username, String password, String email, String role, double balance, String currency, Long customerId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.balance = balance;
        this.currency = currency;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", customerId=" + customerId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(account.balance, balance) == 0 &&
                Objects.equals(id, account.id) &&
                Objects.equals(accountNumber, account.accountNumber) &&
                Objects.equals(username, account.username) &&
                Objects.equals(password, account.password) &&
                Objects.equals(email, account.email) &&
                Objects.equals(role, account.role) &&
                Objects.equals(currency, account.currency) &&
                Objects.equals(customerId, account.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, username, password, email, role, balance, currency, customerId);
    }
}

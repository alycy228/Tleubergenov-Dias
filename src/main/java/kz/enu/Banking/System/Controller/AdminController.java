package kz.enu.Banking.System.Controller;

import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.repository.AccountRepository;
import kz.enu.Banking.System.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AdminController(AccountRepository accountRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", accountRepository.findAll());
        return "admin/admin";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new Account());
        return "admin/create-user";
    }

    @PostMapping("/create")
    public String createUser(Account user) {
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        if (user.getCurrency() == null || user.getCurrency().isBlank()) {
            user.setCurrency("KZT");
        }
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim());
        }
        user.setBalance(0.0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        accountRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        Account user = accountRepository.findById(id).orElse(null);
        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, Account user) {
        Account existing = accountRepository.findById(id).orElse(null);
        if (existing == null) {
            return "redirect:/admin/users";
        }
        String existingEmail = existing.getEmail();
        existing.setUsername(user.getUsername());
        existing.setRole(user.getRole());
        existing.setAccountNumber(user.getAccountNumber());
        existing.setBalance(user.getBalance());
        if (user.getCurrency() != null && !user.getCurrency().isBlank()) {
            existing.setCurrency(user.getCurrency());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existing.setEmail(user.getEmail().trim());
        }
        boolean passwordChanged = false;
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
            passwordChanged = true;
        }
        accountRepository.save(existing);
        if (passwordChanged) {
            String targetEmail = (existingEmail != null && !existingEmail.isBlank())
                    ? existingEmail
                    : existing.getEmail();
            if (targetEmail != null && !targetEmail.isBlank()) {
                try {
                    emailService.sendEmail(
                            targetEmail,
                            "Password changed",
                            "Your password has been changed. If this wasn't you, contact support."
                    );
                } catch (RuntimeException ex) {
                    // Ignore email failures to avoid breaking admin update flow.
                }
            }
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        accountRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}

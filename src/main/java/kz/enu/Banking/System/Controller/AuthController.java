package kz.enu.Banking.System.Controller;

import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.repository.AccountRepository;
import kz.enu.Banking.System.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AccountRepository repo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;

    public AuthController(AccountRepository repo, PasswordEncoder encoder, EmailService emailService) {
        this.repo = repo;
        this.encoder = encoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public String register(@Valid Account account, BindingResult bindingResult) {
        if (account.getEmail() != null) {
            account.setEmail(account.getEmail().trim());
        }
        if (account.getEmail() == null || account.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "email.required", "Email is required");
        } else if (repo.findByEmail(account.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "email.exists", "Email already in use");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        account.setBalance(0.0);
        if (account.getCurrency() == null || account.getCurrency().isBlank()) {
            account.setCurrency("KZT");
        }
        account.setPassword(encoder.encode(account.getPassword()));
        account.setRole("USER");
        repo.save(account);
        if (account.getEmail() != null && !account.getEmail().isBlank()) {
            try {
                emailService.sendEmail(
                        account.getEmail(),
                        "reg ",
                        "Аккаунт создан"
                );
            } catch (RuntimeException ex) {
                // Ignore email failures to avoid breaking registration flow.
            }
        }
        return "redirect:/login";
    }
}

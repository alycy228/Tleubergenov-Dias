package kz.enu.Banking.System.Controller;

import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AccountRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AccountRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public String register(@Valid Account account, BindingResult bindingResult) {
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
        return "redirect:/login";
    }
}

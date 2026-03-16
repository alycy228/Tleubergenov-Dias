package kz.enu.Banking.System.Controller;

import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final AccountRepository accountRepository;

    public PageController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping({"/", "/index", "/index.html"})
    public String index() {
        return "redirect:/home";
    }

    @GetMapping({"/home", "/home.html"})
    public String home(Model model, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);
        return "home";
    }

    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "login";
    }

    @GetMapping({"/register", "/register.html"})
    public String registerPage() {
        return "register";
    }

    @GetMapping({"/profile", "/profile.html"})
    public String profile(Model model, Authentication auth) {
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username).orElse(null);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("username", username);
        if (account != null) {
            model.addAttribute("balance", account.getBalance());
            model.addAttribute("currency", account.getCurrency());
            model.addAttribute("accountNumber", account.getAccountNumber());
        }
        return "profile";
    }

    @PostMapping("/profile/currency")
    public String updateCurrency(@RequestParam String currency, Authentication auth) {
        String username = auth.getName();
        Account account = accountRepository.findByUsername(username).orElse(null);
        if (account == null) {
            return "redirect:/profile";
        }
        account.setCurrency(currency);
        accountRepository.save(account);
        return "redirect:/profile";
    }
}

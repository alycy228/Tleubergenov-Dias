package kz.enu.Banking.System.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.enu.Banking.System.Models.Account;
import kz.enu.Banking.System.repository.AccountRepository;
import kz.enu.Banking.System.service.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoginNotificationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountRepository accountRepository;
    private final EmailService emailService;

    public LoginNotificationSuccessHandler(AccountRepository accountRepository, EmailService emailService) {
        this.accountRepository = accountRepository;
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username).orElse(null);
        if (account != null && account.getEmail() != null && !account.getEmail().isBlank()) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String ip = request.getRemoteAddr();
            try {
                emailService.sendEmail(
                        account.getEmail(),
                        "log",
                        "login: " + time + "\nIP: " + ip
                );
            } catch (RuntimeException ex) {
            }
        }
        response.sendRedirect("/home");
    }
}

package kz.enu.Banking.System.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.from-name:}")
    private String fromName;
    @Value("${spring.mail.username:}")
    private String username;
    public void sendEmail(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        if (username != null && !username.isBlank()) {
            if (fromName != null && !fromName.isBlank()) {
                message.setFrom(fromName + " <" + username + ">");
            } else {
                message.setFrom(username);
            }
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

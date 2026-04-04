package kz.enu.Banking.System.Controller;

import kz.enu.Banking.System.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/send")
    public String send(){
        emailService.sendEmail(
                "user@mail.com",
                "Hello",
                "Test message"
        );
        return "Email sent";
    }
}


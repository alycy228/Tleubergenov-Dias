package kz.enu.Banking.System.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class RestUserController {

    @GetMapping
    public List<Map<String, Object>> list() {
        return List.of(Map.of("id", 1, "username", "demo"));
    }
}

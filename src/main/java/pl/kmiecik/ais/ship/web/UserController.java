package pl.kmiecik.ais.ship.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
class UserController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
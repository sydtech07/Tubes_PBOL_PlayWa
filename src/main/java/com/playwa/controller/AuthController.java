package com.playwa.controller;

import com.playwa.model.Player;
import com.playwa.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @GetMapping("/login")
    public String loginPage(Model m) { m.addAttribute("mode", "login"); return "login"; }

    @GetMapping("/register")
    public String registerPage(Model m) { m.addAttribute("mode", "register"); return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model m) {
        Optional<Player> player = auth.login(username, password);
        if (player.isEmpty()) {
            m.addAttribute("mode", "login").addAttribute("error", "Username atau password salah.");
            return "login";
        }
        session.setAttribute("playerId", player.get().getId());
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String characterName, HttpSession session, Model m) {
        try {
            Player player = auth.register(username, password, characterName);
            session.setAttribute("playerId", player.getId());
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            m.addAttribute("mode", "register").addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/login"; }
}

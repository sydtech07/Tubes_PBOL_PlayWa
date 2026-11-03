package com.playwa.service;

import com.playwa.model.Player;
import com.playwa.repository.PlayerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/** Register dan login. Password disimpan sebagai hash BCrypt, bukan teks asli. */
@Service
@Transactional
public class AuthService {
    private final PlayerRepository players;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(PlayerRepository players) { this.players = players; }

    /** Melempar IllegalArgumentException berisi pesan yang bisa ditampilkan ke pemain. */
    public Player register(String username, String password, String characterName) {
        String user = username == null ? "" : username.trim().toLowerCase();
        String hero = characterName == null ? "" : characterName.trim();
        if (!user.matches("[a-z0-9_]{3,20}"))
            throw new IllegalArgumentException("Username 3-20 karakter: huruf kecil, angka, atau _.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        if (hero.length() < 3 || hero.length() > 20)
            throw new IllegalArgumentException("Nama karakter 3-20 karakter.");
        if (players.findByUsername(user).isPresent())
            throw new IllegalArgumentException("Username sudah dipakai.");
        return players.save(new Player(hero, user, encoder.encode(password)));
    }

    public Optional<Player> login(String username, String password) {
        String user = username == null ? "" : username.trim().toLowerCase();
        return players.findByUsername(user)
            .filter(p -> password != null && encoder.matches(password, p.getPasswordHash()));
    }
}

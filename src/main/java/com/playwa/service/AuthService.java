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
    static final int MIN_PASSWORD_LENGTH = 8;
    /** BCrypt hanya membaca 72 byte pertama. Karena karakter password dibatasi ASCII, 1 karakter = 1 byte. */
    static final int MAX_PASSWORD_LENGTH = 72;

    private final PlayerRepository players;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(PlayerRepository players) { this.players = players; }

    /** Melempar IllegalArgumentException berisi pesan yang bisa ditampilkan ke pemain. */
    public Player register(String username, String password, String confirmPassword, String characterName) {
        String user = username == null ? "" : username.trim().toLowerCase();
        String hero = characterName == null ? "" : characterName.trim();
        if (user.isEmpty())
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        if (!user.matches("[a-z0-9_]{3,20}"))
            throw new IllegalArgumentException("Username 3-20 karakter: huruf kecil, angka, atau _.");
        if (hero.isEmpty())
            throw new IllegalArgumentException("Nama karakter tidak boleh kosong.");
        if (hero.length() < 3 || hero.length() > 20)
            throw new IllegalArgumentException("Nama karakter 3-20 karakter.");
        validatePassword(password);
        if (!password.equals(confirmPassword))
            throw new IllegalArgumentException("Konfirmasi password tidak sama.");
        if (players.findByUsername(user).isPresent())
            throw new IllegalArgumentException("Username sudah dipakai.");
        return players.save(new Player(hero, user, encoder.encode(password)));
    }

    static void validatePassword(String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        for (char c : password.toCharArray()) {
            boolean allowed = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9') || c == '.' || c == '_';
            if (!allowed)
                throw new IllegalArgumentException("Password hanya boleh huruf, angka, titik (.) dan underscore (_).");
        }
        if (password.length() < MIN_PASSWORD_LENGTH)
            throw new IllegalArgumentException("Password minimal " + MIN_PASSWORD_LENGTH + " karakter.");
        if (password.length() > MAX_PASSWORD_LENGTH)
            throw new IllegalArgumentException("Password terlalu panjang (maksimal " + MAX_PASSWORD_LENGTH + " karakter).");
        boolean lower = false, digit = false, symbol = false;
        for (char c : password.toCharArray()) {
            if (c >= 'a' && c <= 'z') lower = true;
            else if (c >= '0' && c <= '9') digit = true;
            else if (c == '.' || c == '_') symbol = true;
        }
        if (!lower) throw new IllegalArgumentException("Password harus mengandung huruf kecil.");
        if (!digit) throw new IllegalArgumentException("Password harus mengandung angka.");
        if (!symbol) throw new IllegalArgumentException("Password harus mengandung simbol titik (.) atau underscore (_).");
    }

    public Optional<Player> login(String username, String password) {
        String user = username == null ? "" : username.trim().toLowerCase();
        if (user.isEmpty() || password == null || password.isEmpty())
            throw new IllegalArgumentException("Username dan password wajib diisi.");
        return players.findByUsername(user)
                .filter(p -> encoder.matches(password, p.getPasswordHash()));
    }
}
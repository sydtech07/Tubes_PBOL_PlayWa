package com.playwa.service;

import com.playwa.model.Player;
import com.playwa.repository.InventorySlotRepository;
import com.playwa.repository.PlayerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Pengelolaan akun: ganti nama (berbayar), reset ke awal, dan hapus akun. */
@Service
@Transactional
public class AccountService {
    public static final int RENAME_COST = 100;

    private final PlayerRepository players;
    private final InventorySlotRepository slots;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AccountService(PlayerRepository players, InventorySlotRepository slots) {
        this.players = players;
        this.slots = slots;
    }

    /** Melempar IllegalArgumentException berisi pesan yang bisa ditampilkan ke pemain. */
    public String renameCharacter(Player player, String newName) {
        String name = newName == null ? "" : newName.trim();
        if (name.length() < 3 || name.length() > 20)
            throw new IllegalArgumentException("Nama karakter 3-20 karakter.");
        if (name.equals(player.getName()))
            throw new IllegalArgumentException("Nama baru sama dengan nama sekarang.");
        if (!player.spendGold(RENAME_COST))
            throw new IllegalArgumentException("Gold tidak cukup (butuh " + RENAME_COST + ", punya " + player.getGold() + ").");
        player.rename(name);
        players.save(player);
        return "Nama karakter diganti menjadi " + name + " (-" + RENAME_COST + " gold).";
    }

    /** Hapus seluruh inventori dan kembalikan karakter ke level 1. Wajib konfirmasi password. */
    public String resetAccount(Player player, String password) {
        verifyPassword(player, password);
        slots.deleteAll(slots.findByPlayer(player));
        player.resetProgress();
        players.save(player);
        return "Akun direset. Petualanganmu dimulai dari awal.";
    }

    /** Hapus akun dan seluruh datanya secara permanen. Wajib konfirmasi password. */
    public void deleteAccount(Player player, String password) {
        verifyPassword(player, password);
        slots.deleteAll(slots.findByPlayer(player));
        players.deleteById(player.getId());
    }

    private void verifyPassword(Player player, String password) {
        if (password == null || !encoder.matches(password, player.getPasswordHash()))
            throw new IllegalArgumentException("Password salah.");
    }
}

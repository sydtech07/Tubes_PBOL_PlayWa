package com.playwa.service;

import com.playwa.activity.Activity;
import com.playwa.activity.ActivityResult;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Jembatan: jalankan Activity (logika OOP murni) lalu simpan hasilnya ke database. */
@Service
@Transactional
public class GameService {
    private final PlayerRepository players;
    private final ItemRepository items;
    private final Inventory inventory;

    public GameService(PlayerRepository players, ItemRepository items, Inventory inventory) {
        this.players = players;
        this.items = items;
        this.inventory = inventory;
    }

    public ActivityResult perform(Player player, Activity activity) {
        ActivityResult result = activity.run(player);
        for (Item loot : result.getLoot()) {
            Item saved = items.findByName(loot.getName()).orElse(null);
            if (saved != null) inventory.add(player, saved, 1);
            else result.log("(" + loot.getName() + " belum ada di tabel item)");
        }
        players.save(player);
        return result;
    }

    public String rest(Player player) {
        player.rest();
        players.save(player);
        return "Kamu beristirahat. Stamina penuh, HP " + player.getHp() + "/" + player.getMaxHp();
    }
}

package com.playwa.service;

import com.playwa.activity.Activity;
import com.playwa.activity.ActivityResult;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.Map;

/** Jembatan: jalankan Activity (logika OOP murni) lalu simpan hasilnya ke database. */
@Service
@Transactional
public class GameService {
    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

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

        // Gabungkan loot dengan nama sama supaya jumlahnya tepat dan hanya satu update per item.
        Map<String, Integer> lootCount = new LinkedHashMap<>();
        for (Item loot : result.getLoot()) lootCount.merge(loot.getName(), 1, Integer::sum);

        lootCount.forEach((name, qty) -> {
            Item saved = items.findByName(name).orElse(null);
            if (saved == null) {
                LOG.error("Loot '{}' x{} gagal masuk inventory: nama tidak ada di tabel item.", name, qty);
                result.log("(" + name + " x" + qty + " gagal masuk inventory: item belum ada di database)");
            } else {
                inventory.add(player, saved, qty);
            }
        });
        players.save(player);
        return result;
    }

    public String rest(Player player) {
        player.rest();
        players.save(player);
        return "Kamu beristirahat. Stamina penuh, HP " + player.getHp() + "/" + player.getMaxHp();
    }
}
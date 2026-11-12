
package com.playwa.service;

import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.playwa.repository.PlayerRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Crafting: gabungkan material menjadi item baru. */
@Service
@Transactional
public class Crafting {

    public record Recipe(String result, Map<String, Integer> ingredients) {}

    // Tambahan: peluang bonus dan EXP crafting
    private static final Random RNG = new Random();
    private static final int BONUS_CHANCE = 10;
    private static final int CRAFT_EXP = 5;

    private static final List<Recipe> RECIPES = List.of(
            new Recipe("Pedang Kayu", Map.of("Kayu", 3)),
            new Recipe("Tombak Kayu", Map.of("Kayu", 3, "Batu", 1)),
            new Recipe("Kapak Batu", Map.of("Kayu", 2, "Batu", 3)),
            new Recipe("Belati Karat", Map.of("Besi", 1, "Kayu", 1)),
            new Recipe("Pedang Besi", Map.of("Besi", 3, "Kayu", 1)),
            new Recipe("Tombak Prajurit", Map.of("Besi", 2, "Kayu", 2)),
            new Recipe("Pedang Goblin", Map.of("Kulit Goblin", 2, "Besi", 2)),
            new Recipe("Pedang Baja", Map.of("Besi", 5, "Bijih Perak", 2)),
            new Recipe("Topi Kulit", Map.of("Kulit Slime", 2)),
            new Recipe("Baju Kulit", Map.of("Kulit Goblin", 3)),
            new Recipe("Sepatu Kulit", Map.of("Kulit Slime", 2, "Kayu", 1)),
            new Recipe("Helm Besi", Map.of("Besi", 3)),
            new Recipe("Sepatu Besi", Map.of("Besi", 2, "Kulit Goblin", 1)),
            new Recipe("Zirah Besi", Map.of("Besi", 6, "Kulit Goblin", 2)),
            new Recipe("Potion Kecil", Map.of("Tanaman Herbal", 2)),
            new Recipe("Potion Besar", Map.of("Tanaman Herbal", 4, "Kulit Slime", 1)),
            new Recipe("Sup Jamur", Map.of("Jamur Hutan", 3, "Tanaman Herbal", 1))
    );

    private final ItemRepository items;
    private final PlayerRepository players;
    private final Inventory inventory;

    public Crafting(ItemRepository items, Inventory inventory,
                    PlayerRepository players) {
        this.items = items;
        this.inventory = inventory;
        this.players = players;
    }

    public List<Recipe> recipes() {
        return RECIPES;
    }

    public String craftItem(Player player, String resultName) {

        // Mencari resep crafting
        Recipe recipe = RECIPES.stream()
                .filter(r -> r.result().equals(resultName))
                .findFirst()
                .orElse(null);

        Item result = items.findByName(resultName).orElse(null);

        if (recipe == null || result == null) {
            return "Resep " + resultName + " tidak ditemukan.";
        }

        // Memeriksa ketersediaan bahan
        Map<Item, Integer> needs = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> e : recipe.ingredients().entrySet()) {

            Item ingredient = items.findByName(e.getKey()).orElse(null);

            if (ingredient == null ||
                    inventory.count(player, ingredient) < e.getValue()) {

                return "Bahan kurang: " + e.getKey()
                        + " x" + e.getValue();
            }

            needs.put(ingredient, e.getValue());
        }

        // Mengurangi bahan yang digunakan
        needs.forEach((item, qty) ->
                inventory.remove(player, item, qty)
        );

        // Peluang mendapatkan bonus item 10%
        boolean bonus = RNG.nextInt(100) < BONUS_CHANCE;
        int jumlahHasil = bonus ? 2 : 1;

        // Menambahkan hasil crafting ke inventory
        inventory.add(player, result, jumlahHasil);

        // Memberikan EXP crafting
        boolean levelUp = player.gainExp(CRAFT_EXP);
        players.save(player);

        // Menampilkan hasil crafting
        String pesan = "Berhasil membuat " + resultName
                + "! +" + CRAFT_EXP + " EXP.";

        if (bonus) {
            pesan += " BONUS! Mendapatkan 1 item tambahan!";
        }

        if (levelUp) {
            pesan += " LEVEL UP! Sekarang level "
                    + player.getLevel() + ".";
        }

        return pesan;
    }
}

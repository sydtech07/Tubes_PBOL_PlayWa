package com.playwa.activity;

import com.playwa.model.Item;
import com.playwa.model.Material;
import com.playwa.model.Player;
import java.util.Random;

/** Explore: event acak (item, gold, kosong, atau jebakan). */
public class Adventure extends Activity {
    private static final Random RNG = new Random();
    private static final String[] MATERIALS = {"Kayu", "Batu", "Besi", "Tanaman Herbal", "Jamur Hutan"};
    private static final int[] PRICES = {5, 8, 15, 6, 7};

    public Adventure() { super("Explore", 10); }

    private static final String[] PREAMBLE = {
        "Kamu menyusuri jalan setapak yang sepi...", "Angin berbisik di antara pepohonan...",
        "Langkahmu menggema di tanah berbatu...", "Kamu menyibak semak-semak yang lebat...",
        "Sesuatu berkilau di kejauhan...", "Suara ranting patah terdengar di belakangmu..."};

    @Override
    protected void execute(Player player, ActivityResult result) {
        int first = RNG.nextInt(PREAMBLE.length);
        result.log(PREAMBLE[first]);
        result.log(PREAMBLE[(first + 1 + RNG.nextInt(PREAMBLE.length - 1)) % PREAMBLE.length]);
        int roll = RNG.nextInt(100);
        if (roll < 45) {
            result.addLoot(randomMaterial());
            result.addExp(5);
            result.log("Tanganmu menyentuh sesuatu... ada yang bisa dibawa pulang!");
        } else if (roll < 70) {
            result.addGold(10 + RNG.nextInt(21));
            result.addExp(3);
            result.log("Sebuah kantong kecil tergeletak di tanah!");
        } else if (roll < 90) {
            result.addExp(2);
            result.log("Tidak ada apa-apa di sini. Kamu melanjutkan perjalanan.");
        } else {
            int dmg = 5 + RNG.nextInt(11);
            player.takeDamage(dmg);
            result.log("Klik! Kamu menginjak jebakan! \"Akh!\" (HP -" + dmg + ")");
        }
    }

    private Item randomMaterial() {
        int i = RNG.nextInt(MATERIALS.length);
        return new Material(MATERIALS[i], PRICES[i]);
    }
}

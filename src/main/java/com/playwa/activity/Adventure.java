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

    @Override
    protected void execute(Player player, ActivityResult result) {
        int roll = RNG.nextInt(100);
        if (roll < 45) {
            Item item = randomMaterial();
            result.addLoot(item);
            result.addExp(5);
            result.log("Kamu menemukan " + item.getName() + "!");
        } else if (roll < 70) {
            int gold = 10 + RNG.nextInt(21);
            result.addGold(gold);
            result.addExp(3);
            result.log("Kamu menemukan kantong berisi " + gold + " gold!");
        } else if (roll < 90) {
            result.addExp(2);
            result.log("Kamu berjalan jauh, tapi tidak menemukan apa-apa.");
        } else {
            int dmg = 5 + RNG.nextInt(11);
            player.takeDamage(dmg);
            result.log("Kamu terkena jebakan! HP -" + dmg);
        }
    }

    private Item randomMaterial() {
        int i = RNG.nextInt(MATERIALS.length);
        return new Material(MATERIALS[i], PRICES[i]);
    }
}

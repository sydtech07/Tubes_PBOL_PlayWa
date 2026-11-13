package com.playwa.service;

import com.playwa.model.Item;
import com.playwa.model.Material;
import com.playwa.model.Player;
import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Shop: jual beli. Beli = Weapon/Potion. Jual = item apa saja (harga 50%). */
@Service
@Transactional
public class Shop {
    private final ItemRepository items;
    private final Inventory inventory;
    private final PlayerRepository players;

    public Shop(ItemRepository items, Inventory inventory, PlayerRepository players) {
        this.items = items;
        this.inventory = inventory;
        this.players = players;
    }

    public List<Item> catalog() {
        return items.findAll().stream().filter(i -> !(i instanceof Material)).toList();
    }

    public int sellPrice(Item item) { return Math.max(1, item.getPrice() / 2); }

    public String buyItem(Player player, String itemName, int qty) {
        if (qty <= 0) return "Jumlah tidak valid.";
        Item item = items.findByName(itemName).orElse(null);
        if (item == null || item instanceof Material) return itemName + " tidak dijual di toko.";
        // long supaya harga x jumlah tidak overflow jadi negatif (yang bikin gold malah bertambah)
        long totalLong = (long) item.getPrice() * qty;
        if (totalLong > Integer.MAX_VALUE) return "Jumlah pembelian terlalu besar.";
        int total = (int) totalLong;
        if (player.getGold() < total)
            return "Gold tidak cukup (butuh " + total + ", punya " + player.getGold() + ").";
        if (!player.spendGold(total)) return "Pembelian gagal.";
        inventory.add(player, item, qty);
        players.save(player);
        return "Membeli " + qty + "x " + itemName + " seharga " + total + " gold.";
    }

    public String sellItem(Player player, String itemName, int qty) {
        if (qty <= 0) return "Jumlah tidak valid.";
        Item item = items.findByName(itemName).orElse(null);
        if (item == null) return itemName + " tidak dikenal.";
        if (!inventory.remove(player, item, qty)) return "Kamu tidak punya " + qty + "x " + itemName + ".";
        if (inventory.count(player, item) == 0) player.unequipByName(itemName);
        int total = (int) Math.min((long) sellPrice(item) * qty, Integer.MAX_VALUE);
        player.addGold(total);
        players.save(player);
        return "Menjual " + qty + "x " + itemName + " dan mendapat " + total + " gold.";
    }
}
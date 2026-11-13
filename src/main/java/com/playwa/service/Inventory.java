package com.playwa.service;

import com.playwa.model.InventorySlot;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.model.Usable;
import com.playwa.repository.InventorySlotRepository;
import com.playwa.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/** Inventory: mengatur item milik pemain. */
@Service
@Transactional
public class Inventory {
    private final InventorySlotRepository slots;
    private final PlayerRepository players;

    public Inventory(InventorySlotRepository slots, PlayerRepository players) {
        this.slots = slots;
        this.players = players;
    }

    public void add(Player player, Item item, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Jumlah item harus lebih dari 0.");
        Optional<InventorySlot> found = slots.findByPlayerAndItem(player, item);
        if (found.isPresent()) {
            found.get().add(qty);
            slots.save(found.get());
        } else {
            slots.save(new InventorySlot(player, item, qty));
        }
    }

    public boolean remove(Player player, Item item, int qty) {
        if (qty <= 0) return false;   // BARU
        Optional<InventorySlot> found = slots.findByPlayerAndItem(player, item);
        if (found.isEmpty() || !found.get().remove(qty)) return false;
        if (found.get().getQuantity() == 0) slots.delete(found.get());
        else slots.save(found.get());
        return true;
    }

    public int count(Player player, Item item) {
        return slots.findByPlayerAndItem(player, item).map(InventorySlot::getQuantity).orElse(0);
    }

    public List<InventorySlot> list(Player player) { return slots.findByPlayer(player); }

    /** Polymorphism: Potion habis dipakai, Weapon di-equip dan tetap ada. */
    public String useItem(Player player, Item item) {
        if (!(item instanceof Usable usable)) return item.getName() + " tidak bisa dipakai.";
        if (count(player, item) < 1) return "Kamu tidak punya " + item.getName() + ".";
        String message = usable.use(player);
        if (usable.isConsumedOnUse()) remove(player, item, 1);
        players.save(player);
        return message;
    }
}
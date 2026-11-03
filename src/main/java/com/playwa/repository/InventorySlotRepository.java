package com.playwa.repository;

import com.playwa.model.InventorySlot;
import com.playwa.model.Item;
import com.playwa.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventorySlotRepository extends JpaRepository<InventorySlot, Long> {
    Optional<InventorySlot> findByPlayerAndItem(Player player, Item item);
    List<InventorySlot> findByPlayer(Player player);
}

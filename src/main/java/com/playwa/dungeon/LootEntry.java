package com.playwa.dungeon;

/** Satu baris tabel drop: nama item, peluang (%), jumlah min-max. */
public record LootEntry(String itemName, int chancePercent, int minQty, int maxQty) {
    public LootEntry {
        if (itemName == null || itemName.isBlank())
            throw new IllegalArgumentException("Nama item loot tidak boleh kosong.");
        if (chancePercent < 0 || chancePercent > 100)
            throw new IllegalArgumentException(itemName + ": peluang harus 0-100.");
        if (minQty < 1 || maxQty < minQty)
            throw new IllegalArgumentException(itemName + ": jumlah harus minimal 1 dan max >= min.");
    }
}
package com.playwa.dungeon;

/** Satu baris tabel drop: nama item, peluang (%), jumlah min-max. */
public record LootEntry(String itemName, int chancePercent, int minQty, int maxQty) {}

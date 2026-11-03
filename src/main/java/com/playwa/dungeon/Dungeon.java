package com.playwa.dungeon;

import com.playwa.model.monster.Monster;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

/** Data satu dungeon: syarat, jumlah lantai, monster, boss, dan tabel drop. */
public class Dungeon {
    private static final Random RNG = new Random();
    private final String key, name, description;
    private final int minLevel, staminaCost, floors;
    private final List<Supplier<Monster>> monsters;
    private final Supplier<Monster> boss;
    private final List<LootEntry> lootTable, bossLoot;

    public Dungeon(String key, String name, String description, int minLevel, int staminaCost, int floors,
                   List<Supplier<Monster>> monsters, Supplier<Monster> boss,
                   List<LootEntry> lootTable, List<LootEntry> bossLoot) {
        this.key = key; this.name = name; this.description = description;
        this.minLevel = minLevel; this.staminaCost = staminaCost; this.floors = floors;
        this.monsters = monsters; this.boss = boss; this.lootTable = lootTable; this.bossLoot = bossLoot;
    }

    public Monster spawnMonster() { return monsters.get(RNG.nextInt(monsters.size())).get(); }
    public Monster spawnBoss() { return boss.get(); }
    public String getBossName() { return boss.get().getName(); }

    public String getLootSummary() {
        Set<String> names = new LinkedHashSet<>();
        lootTable.forEach(e -> names.add(e.itemName()));
        bossLoot.forEach(e -> names.add(e.itemName()));
        return String.join(", ", names);
    }

    public String getKey() { return key; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMinLevel() { return minLevel; }
    public int getStaminaCost() { return staminaCost; }
    public int getFloors() { return floors; }
    public List<LootEntry> getLootTable() { return lootTable; }
    public List<LootEntry> getBossLoot() { return bossLoot; }
}

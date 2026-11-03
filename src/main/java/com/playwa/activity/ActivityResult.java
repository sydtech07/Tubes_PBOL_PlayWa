package com.playwa.activity;

import com.playwa.model.Item;
import java.util.ArrayList;
import java.util.List;

/** Hasil satu aktivitas: log cerita, hadiah EXP/Gold, dan loot. */
public class ActivityResult {
    private final List<String> logs = new ArrayList<>();
    private final List<Item> loot = new ArrayList<>();
    private int exp, gold;
    private boolean leveledUp;

    public void log(String message) { logs.add(message); }
    public void addLoot(Item item) { loot.add(item); }
    public void addExp(int n) { exp += n; }
    public void addGold(int n) { gold += n; }
    public void setLeveledUp(boolean v) { leveledUp = v; }

    public List<String> getLogs() { return logs; }
    public List<Item> getLoot() { return loot; }
    public int getExp() { return exp; }
    public int getGold() { return gold; }
    public boolean isLeveledUp() { return leveledUp; }
}

package com.playwa.model;

import jakarta.persistence.*;

/** Encapsulation: semua atribut private, perubahan lewat method. */
@Entity
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level = 1, exp = 0, gold = 100;
    private int hp = 100, maxHp = 100;
    private int stamina = 50, maxStamina = 50;
    private int baseAttack = 5;
    private int weaponBonus = 0;
    private int helmetDef = 0, chestDef = 0, bootsDef = 0;
    private String weaponName, helmetName, chestName, bootsName;
    @Column(unique = true) private String username;
    private String passwordHash;

    protected Player() {}
    public Player(String name) { this.name = name; }
    public Player(String name, String username, String passwordHash) { this.name = name; this.username = username; this.passwordHash = passwordHash; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    public void equip(Weapon weapon) { weaponBonus = weapon.getAttackPower(); weaponName = weapon.getName(); }
    public int getTotalAttack() { return baseAttack + weaponBonus; }
    public int getWeaponBonus() { return weaponBonus; }
    public void equip(Armor armor) {
        switch (armor.getSlot()) {
            case HELMET -> { helmetDef = armor.getDefense(); helmetName = armor.getName(); }
            case CHEST -> { chestDef = armor.getDefense(); chestName = armor.getName(); }
            case BOOTS -> { bootsDef = armor.getDefense(); bootsName = armor.getName(); }
        }
    }
    public void unequip(String slot) {
        switch (slot) {
            case "weapon" -> { weaponBonus = 0; weaponName = null; }
            case "helmet" -> { helmetDef = 0; helmetName = null; }
            case "chest" -> { chestDef = 0; chestName = null; }
            case "boots" -> { bootsDef = 0; bootsName = null; }
            default -> { }
        }
    }
    public void unequipByName(String itemName) {
        if (itemName.equals(weaponName)) unequip("weapon");
        if (itemName.equals(helmetName)) unequip("helmet");
        if (itemName.equals(chestName)) unequip("chest");
        if (itemName.equals(bootsName)) unequip("boots");
    }
    public boolean isEquipped(String itemName) {
        return itemName.equals(weaponName) || itemName.equals(helmetName)
            || itemName.equals(chestName) || itemName.equals(bootsName);
    }
    public String getWeaponName() { return weaponName; }
    public String getHelmetName() { return helmetName; }
    public String getChestName() { return chestName; }
    public String getBootsName() { return bootsName; }
    public int getTotalDefense() { return helmetDef + chestDef + bootsDef; }
    public void restoreStamina(int amount) { stamina = Math.min(maxStamina, stamina + amount); }

    public int expToNextLevel() { return level * 100; }

    public boolean gainExp(int amount) {
        exp += amount;
        boolean leveledUp = false;
        while (exp >= expToNextLevel()) {
            exp -= expToNextLevel();
            level++; maxHp += 20; baseAttack += 2; hp = maxHp;
            leveledUp = true;
        }
        return leveledUp;
    }

    public void heal(int amount) { hp = Math.min(maxHp, hp + amount); }
    public void takeDamage(int dmg) { hp = Math.max(0, hp - dmg); }
    public boolean isAlive() { return hp > 0; }
    public void addGold(int amount) { gold += amount; }
    public boolean spendGold(int amount) { if (gold < amount) return false; gold -= amount; return true; }
    public boolean useStamina(int amount) { if (stamina < amount) return false; stamina -= amount; return true; }
    public void rest() { stamina = maxStamina; heal(maxHp / 2); }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getGold() { return gold; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public int getBaseAttack() { return baseAttack; }
}

package com.playwa.demo;

import com.playwa.model.*;
import com.playwa.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import static com.playwa.model.ArmorSlot.*;
import static com.playwa.model.MaterialCategory.*;
import static com.playwa.model.Rarity.*;
import static com.playwa.model.WeaponType.*;

/** Katalog item game. Aman dijalankan berulang: hanya menambah item yang namanya belum ada. */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {
    private final ItemRepository items;

    public DataSeeder(ItemRepository items) { this.items = items; }

    @Override
    public void run(String... args) {
        // Material
        add(new Material("Kayu", WOOD, COMMON, 5));
        add(new Material("Batu", ORE, COMMON, 8));
        add(new Material("Besi", ORE, UNCOMMON, 15));
        add(new Material("Bijih Perak", ORE, RARE, 35));
        add(new Material("Tanaman Herbal", HERB, COMMON, 6));
        add(new Material("Jamur Hutan", HERB, COMMON, 7));
        add(new Material("Kulit Slime", HIDE, COMMON, 12));
        add(new Material("Kulit Goblin", HIDE, UNCOMMON, 12));
        add(new Material("Taring Serigala", HIDE, UNCOMMON, 20));
        add(new Material("Permata Biru", GEM, RARE, 60));
        add(new Material("Inti Naga", GEM, LEGENDARY, 500));
        // Senjata
        add(new Weapon("Pedang Kayu", SWORD, COMMON, 30, 3));
        add(new Weapon("Pedang Besi", SWORD, UNCOMMON, 80, 8));
        add(new Weapon("Pedang Goblin", SWORD, UNCOMMON, 120, 12));
        add(new Weapon("Pedang Baja", SWORD, RARE, 200, 14));
        add(new Weapon("Pedang Naga", SWORD, LEGENDARY, 1500, 40));
        add(new Weapon("Kapak Batu", AXE, COMMON, 45, 5));
        add(new Weapon("Kapak Perang", AXE, RARE, 220, 16));
        add(new Weapon("Tombak Kayu", SPEAR, COMMON, 40, 4));
        add(new Weapon("Tombak Prajurit", SPEAR, UNCOMMON, 110, 10));
        add(new Weapon("Belati Karat", DAGGER, COMMON, 25, 3));
        add(new Weapon("Belati Bayangan", DAGGER, EPIC, 350, 18));
        add(new Weapon("Busur Pendek", BOW, COMMON, 50, 5));
        add(new Weapon("Busur Elf", BOW, RARE, 260, 15));
        add(new Weapon("Tongkat Ranting", STAFF, COMMON, 35, 4));
        add(new Weapon("Tongkat Kristal", STAFF, EPIC, 400, 20));
        // Armor
        add(new Armor("Topi Kulit", HELMET, COMMON, 35, 1));
        add(new Armor("Helm Besi", HELMET, UNCOMMON, 90, 3));
        add(new Armor("Helm Baja", HELMET, RARE, 240, 5));
        add(new Armor("Baju Kulit", CHEST, COMMON, 60, 2));
        add(new Armor("Zirah Besi", CHEST, UNCOMMON, 160, 5));
        add(new Armor("Zirah Baja", CHEST, RARE, 420, 9));
        add(new Armor("Sepatu Kulit", BOOTS, COMMON, 30, 1));
        add(new Armor("Sepatu Besi", BOOTS, UNCOMMON, 85, 2));
        add(new Armor("Sepatu Angin", BOOTS, EPIC, 380, 4));
        // Makanan
        add(new Food("Roti", COMMON, 10, 15, 0));
        add(new Food("Apel", COMMON, 6, 8, 5));
        add(new Food("Daging Panggang", UNCOMMON, 30, 30, 10));
        add(new Food("Ikan Bakar", UNCOMMON, 35, 25, 15));
        add(new Food("Sup Jamur", UNCOMMON, 28, 20, 20));
        add(new Food("Pesta Raja", RARE, 120, 80, 40));
        // Potion
        add(new Potion("Potion Kecil", COMMON, 20, 30));
        add(new Potion("Potion Besar", UNCOMMON, 50, 70));
        add(new Potion("Elixir Agung", RARE, 150, 150));
    }

    private void add(Item item) {
        if (items.findByName(item.getName()).isEmpty()) items.save(item);
    }
}

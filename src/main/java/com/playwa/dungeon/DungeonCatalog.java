package com.playwa.dungeon;

import com.playwa.model.monster.*;
import java.util.List;

/** Daftar dungeon yang ada di game. Tambah dungeon baru cukup menambah satu entri di sini. */
public final class DungeonCatalog {
    private DungeonCatalog() {}

    private static final List<Dungeon> ALL = List.of(
        new Dungeon("gua", "Gua Berlumut", "Gua lembap penuh slime dan goblin. Cocok untuk pemula.",
            1, 20, 3, List.of(Slime::new, Goblin::new), GoblinKing::new,
            List.of(new LootEntry("Kayu", 50, 1, 2), new LootEntry("Batu", 40, 1, 2),
                    new LootEntry("Tanaman Herbal", 35, 1, 2), new LootEntry("Kulit Slime", 30, 1, 1),
                    new LootEntry("Kulit Goblin", 25, 1, 1)),
            List.of(new LootEntry("Besi", 70, 1, 2), new LootEntry("Roti", 50, 1, 1),
                    new LootEntry("Pedang Kayu", 15, 1, 1))),
        new Dungeon("hutan", "Hutan Berbisik", "Hutan gelap dihuni serigala dan laba-laba.",
            3, 25, 4, List.of(Wolf::new, Spider::new), GiantSpider::new,
            List.of(new LootEntry("Taring Serigala", 40, 1, 1), new LootEntry("Jamur Hutan", 50, 1, 3),
                    new LootEntry("Tanaman Herbal", 40, 1, 2), new LootEntry("Kayu", 30, 1, 3),
                    new LootEntry("Apel", 30, 1, 1)),
            List.of(new LootEntry("Busur Pendek", 25, 1, 1), new LootEntry("Sup Jamur", 40, 1, 1),
                    new LootEntry("Belati Bayangan", 5, 1, 1))),
        new Dungeon("tambang", "Tambang Tua", "Tambang terbengkalai, dijaga kerangka dan orc.",
            5, 30, 5, List.of(Skeleton::new, Orc::new), StoneGolem::new,
            List.of(new LootEntry("Batu", 50, 2, 4), new LootEntry("Besi", 40, 1, 2),
                    new LootEntry("Bijih Perak", 20, 1, 1), new LootEntry("Kulit Goblin", 20, 1, 1)),
            List.of(new LootEntry("Permata Biru", 35, 1, 1), new LootEntry("Bijih Perak", 60, 1, 2),
                    new LootEntry("Zirah Besi", 15, 1, 1), new LootEntry("Kapak Perang", 8, 1, 1))),
        new Dungeon("naga", "Sarang Naga", "Sarang terakhir. Hanya petualang tangguh yang pulang.",
            10, 40, 6, List.of(Orc::new, Skeleton::new), DragonLord::new,
            List.of(new LootEntry("Besi", 40, 1, 3), new LootEntry("Bijih Perak", 30, 1, 2),
                    new LootEntry("Permata Biru", 15, 1, 1), new LootEntry("Daging Panggang", 20, 1, 1)),
            List.of(new LootEntry("Inti Naga", 20, 1, 1), new LootEntry("Zirah Baja", 25, 1, 1),
                    new LootEntry("Pesta Raja", 30, 1, 1), new LootEntry("Elixir Agung", 20, 1, 1),
                    new LootEntry("Pedang Naga", 4, 1, 1)))
    );

    public static List<Dungeon> all() { return ALL; }
    public static Dungeon find(String key) {
        return ALL.stream().filter(d -> d.getKey().equals(key)).findFirst().orElseThrow();
    }
}

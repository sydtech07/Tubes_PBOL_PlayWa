
package com.playwa.controller;

import com.playwa.activity.*;
import com.playwa.dungeon.*;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.*;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MVC: menerima klik dari browser, memanggil service,
 * lalu menampilkan template.
 */
@ConditionalOnWebApplication(
        type = ConditionalOnWebApplication.Type.SERVLET
)
@Controller
public class GameController {

    private final PlayerRepository players;
    private final ItemRepository items;
    private final GameService game;
    private final Inventory inventory;
    private final Shop shop;
    private final Crafting crafting;
    private final HttpSession session;

    public GameController(
            PlayerRepository players,
            ItemRepository items,
            GameService game,
            Inventory inventory,
            Shop shop,
            Crafting crafting,
            HttpSession session) {

        this.session = session;
        this.players = players;
        this.items = items;
        this.game = game;
        this.inventory = inventory;
        this.shop = shop;
        this.crafting = crafting;
    }

    public record SlotView(
            String key,
            String label,
            String name,
            String rarity,
            String stat
    ) {}

    public record RecipeView(
            Item item,
            List<String> lines,
            boolean canCraft
    ) {}

    private Player me() {
        Long id = (Long) session.getAttribute("playerId");
        return players.findById(id).orElseThrow();
    }

    private SlotView slot(String key, String label, String name) {
        Item it = name == null
                ? null
                : items.findByName(name).orElse(null);

        return new SlotView(
                key,
                label,
                name,
                it == null
                        ? "common"
                        : it.getRarity().name().toLowerCase(),
                it == null
                        ? ""
                        : it.getDescription()
        );
    }

    private String page(Model m, String name) {
        Player p = me();

        m.addAttribute("player", p)
                .addAttribute("page", name)
                .addAttribute("slots", inventory.list(p));

        m.addAttribute("equipment", List.of(
                slot("weapon", "Senjata", p.getWeaponName()),
                slot("helmet", "Helm", p.getHelmetName()),
                slot("chest", "Zirah", p.getChestName()),
                slot("boots", "Sepatu", p.getBootsName())
        ));

        m.addAttribute(
                "categories",
                List.of("Senjata", "Armor", "Makanan", "Potion", "Material")
        );

        return "game";
    }

    private String back(
            String path,
            RedirectAttributes ra,
            String... lines
    ) {
        ra.addFlashAttribute("logs", List.of(lines));
        return "redirect:" + path;
    }

    @GetMapping("/")
    public String home(Model m) {
        return page(m, "home");
    }

    @GetMapping("/inventory")
    public String inv(Model m) {
        return page(m, "inventory");
    }

    @GetMapping("/shop")
    public String shopPage(Model m) {
        m.addAttribute("catalog", shop.catalog());
        return page(m, "shop");
    }

    @GetMapping("/crafting")
    public String craftPage(Model m) {

        Map<String, Integer> owned = new HashMap<>();

        inventory.list(me()).forEach(sl ->
                owned.put(
                        sl.getItem().getName(),
                        sl.getQuantity()
                )
        );

        List<RecipeView> views = new ArrayList<>();

        for (Crafting.Recipe r : crafting.recipes()) {

            List<String> lines = new ArrayList<>();
            boolean can = true;

            for (Map.Entry<String, Integer> e : r.ingredients().entrySet()) {

                int have = owned.getOrDefault(e.getKey(), 0);

                lines.add(
                        e.getKey() + " " + have + "/" + e.getValue()
                );

                if (have < e.getValue()) {
                    can = false;
                }
            }

            views.add(new RecipeView(
                    items.findByName(r.result()).orElse(null),
                    lines,
                    can
            ));
        }

        m.addAttribute("recipes", views);

        return page(m, "crafting");
    }

    @GetMapping("/dungeon")
    public String dungeonPage(Model m) {
        m.addAttribute("dungeons", DungeonCatalog.all());
        return page(m, "dungeon");
    }

    @PostMapping("/dungeon/enter")
    public String enterDungeon(
            @RequestParam String key,
            RedirectAttributes ra
    ) {
        List<String> logs = game.perform(
                me(),
                new DungeonRun(DungeonCatalog.find(key))
        ).getLogs();

        return back(
                "/dungeon",
                ra,
                logs.toArray(new String[0])
        );
    }

    @PostMapping("/equipment/unequip")
    public String unequip(
            @RequestParam String slot,
            RedirectAttributes ra
    ) {
        Player p = me();

        p.unequip(slot);
        players.save(p);

        return back("/", ra, "Equipment dilepas.");
    }

    @PostMapping("/explore")
    public String explore(RedirectAttributes ra) {

        List<String> logs = game.perform(
                me(),
                new Adventure()
        ).getLogs();

        return back(
                "/",
                ra,
                logs.toArray(new String[0])
        );
    }

    @PostMapping("/hunt")
    public String hunt(RedirectAttributes ra) {

        List<String> logs = game.perform(
                me(),
                new Hunting()
        ).getLogs();

        return back(
                "/",
                ra,
                logs.toArray(new String[0])
        );
    }

    @PostMapping("/rest")
    public String rest(RedirectAttributes ra) {
        return back("/", ra, game.rest(me()));
    }

    @PostMapping("/inventory/use")
    public String use(
            @RequestParam Long itemId,
            RedirectAttributes ra
    ) {
        Item item = items.findById(itemId).orElseThrow();

        return back(
                "/inventory",
                ra,
                inventory.useItem(me(), item)
        );
    }

    @PostMapping("/shop/buy")
    public String buy(
            @RequestParam String name,
            RedirectAttributes ra
    ) {
        return back(
                "/shop",
                ra,
                shop.buyItem(me(), name, 1)
        );
    }

    @PostMapping("/shop/sell")
    public String sell(
            @RequestParam String name,
            RedirectAttributes ra
    ) {
        return back(
                "/shop",
                ra,
                shop.sellItem(me(), name, 1)
        );
    }

    @PostMapping("/crafting/craft")
    public String craft(
            @RequestParam String result,
            RedirectAttributes ra
    ) {
        return back(
                "/crafting",
                ra,
                crafting.craftItem(me(), result)
        );
    }
}

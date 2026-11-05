package com.playwa.ui;

import com.playwa.activity.Activity;
import com.playwa.activity.ActivityResult;
import com.playwa.model.InventorySlot;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.model.Usable;
import com.playwa.service.Crafting;
import com.playwa.ui.ItemBrowser.Entry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/** Jendela utama: top bar, sidebar, dan halaman-halaman game. */
public class MainFrame extends JFrame {
    private final Services s;
    private Player player;
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final Map<String, Page> map = new LinkedHashMap<>();
    private final Map<String, Theme.Btn> nav = new LinkedHashMap<>();
    private final Theme.Bar hp = new Theme.Bar(Theme.HP), sta = new Theme.Bar(Theme.STA);
    private final JLabel hpText = Theme.label("", Theme.SMALL, Theme.TEXT), staText = Theme.label("", Theme.SMALL, Theme.TEXT),
            goldText = Theme.label("", Theme.BOLD, Theme.GOLD), who = Theme.label("", Theme.BOLD, Theme.TEXT),
            lvl = Theme.label("", Theme.SMALL, Theme.MUTED), welcome = Theme.label("", Theme.TITLE, Theme.TEXT),
            toast = Theme.label(" ", Theme.BODY, Theme.GOLD);
    private final Theme.Bar expBar = new Theme.Bar(Theme.EXP);
    private final JLabel expText = Theme.label(" ", Theme.SMALL, Theme.MUTED);
    private final javax.swing.Timer toastTimer = new javax.swing.Timer(4500, e -> toast.setText(" "));

    public MainFrame(Services s, Player player) {
        this.s = s; this.player = player; toastTimer.setRepeats(false);
        restTimer = new javax.swing.Timer(REST_SECONDS_PER_STAMINA * 1000, e -> restTick());
        setTitle("Play W.A"); setSize(1200, 760); setMinimumSize(new Dimension(1050, 660));
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(Theme.BG); setContentPane(root);

        pages.setOpaque(false);
        JPanel center = new JPanel(new BorderLayout(0, 8)); center.setBackground(Theme.BG);
        center.setBorder(new EmptyBorder(16, 20, 10, 20));
        center.add(pages, BorderLayout.CENTER); center.add(toast, BorderLayout.SOUTH);
        root.add(topBar(), BorderLayout.NORTH); root.add(sidebar(), BorderLayout.WEST); root.add(center, BorderLayout.CENTER);

        Theme.Log homeLog = new Theme.Log("Catatan Petualangan"), dungeonLog = new Theme.Log("Laporan Dungeon");
        home = new HomePage(this, homeLog);
        addPage("Home", welcome, home);
        addPage("Dungeon", Theme.label("Dungeon", Theme.TITLE, Theme.TEXT), new DungeonPage(this, dungeonLog));
        addPage("Inventory", Theme.label("Inventory", Theme.TITLE, Theme.TEXT), new ItemBrowser(this::inventoryEntries));
        addPage("Shop", Theme.label("Shop", Theme.TITLE, Theme.TEXT), shopPage());
        addPage("Crafting", Theme.label("Crafting", Theme.TITLE, Theme.TEXT), new ItemBrowser(this::craftEntries));
        reload(); show("Home");
    }

    public Player player() { return player; }
    public Services services() { return s; }

    private <T extends JComponent & Page> void addPage(String key, JLabel title, T page) {
        JPanel wrap = new JPanel(new BorderLayout(0, 14)); wrap.setOpaque(false);
        wrap.add(title, BorderLayout.NORTH); wrap.add(page, BorderLayout.CENTER);
        pages.add(wrap, key); map.put(key, page);
        Theme.Btn b = new Theme.Btn(key, Theme.SIDE); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42)); b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(e -> show(key)); nav.put(key, b); navBox.add(b); navBox.add(Box.createVerticalStrut(4));
    }

    private final JPanel navBox = new JPanel();

    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout()); bar.setBackground(new Color(0x0C0D11));
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE), new EmptyBorder(10, 22, 10, 22)));
        bar.add(Theme.label("PLAY W.A", Theme.serif(Font.BOLD, 22), Theme.GOLD), BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4)); right.setOpaque(false);
        right.add(Theme.label("HP", Theme.SMALL, Theme.MUTED)); right.add(hp); right.add(hpText);
        right.add(Theme.label("Stamina", Theme.SMALL, Theme.MUTED)); right.add(sta); right.add(staText); right.add(goldText);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel sidebar() {
        JPanel side = new JPanel(new BorderLayout()); side.setBackground(Theme.SIDE);
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.LINE), new EmptyBorder(18, 14, 16, 14)));
        JPanel head = new JPanel(); head.setOpaque(false); head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setBorder(new EmptyBorder(0, 6, 16, 0)); head.add(who); head.add(lvl);
        expBar.setPreferredSize(new Dimension(150, 8)); expBar.setMaximumSize(new Dimension(150, 8)); expBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        Theme.Divider line = new Theme.Divider(); line.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(Box.createVerticalStrut(8)); head.add(expBar); head.add(Box.createVerticalStrut(4)); head.add(expText);
        head.add(Box.createVerticalStrut(14)); head.add(line);
        navBox.setOpaque(false); navBox.setLayout(new BoxLayout(navBox, BoxLayout.Y_AXIS));
        Theme.Btn out = new Theme.Btn("Logout", new Color(0x2B3142));
        out.addActionListener(e -> { new AuthFrame(s).setVisible(true); dispose(); });
        side.add(head, BorderLayout.NORTH); side.add(navBox, BorderLayout.CENTER); side.add(out, BorderLayout.SOUTH);
        return side;
    }

    private ShopPage shopPage() {
        ItemBrowser buy = new ItemBrowser(this::buyEntries), sell = new ItemBrowser(this::sellEntries);
        CardLayout cl = new CardLayout(); JPanel body = new JPanel(cl); body.setOpaque(false);
        body.add(buy, "buy"); body.add(sell, "sell");
        Theme.Btn b1 = new Theme.Btn("Beli", new Color(0x3A4160)), b2 = new Theme.Btn("Jual", Theme.PANEL2);
        b1.addActionListener(e -> { cl.show(body, "buy"); b1.setBase(new Color(0x3A4160)); b2.setBase(Theme.PANEL2); });
        b2.addActionListener(e -> { cl.show(body, "sell"); b2.setBase(new Color(0x3A4160)); b1.setBase(Theme.PANEL2); });
        JPanel tg = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); tg.setOpaque(false); tg.add(b1); tg.add(b2);
        return new ShopPage(tg, body, buy, sell);
    }

    private static class ShopPage extends JPanel implements Page {
        private final ItemBrowser buy, sell;
        ShopPage(JPanel toggles, JPanel body, ItemBrowser buy, ItemBrowser sell) {
            super(new BorderLayout(0, 12)); this.buy = buy; this.sell = sell; setOpaque(false);
            add(toggles, BorderLayout.NORTH); add(body, BorderLayout.CENTER);
        }
        @Override public void refresh() { buy.refresh(); sell.refresh(); }
    }

    // ---- data untuk ItemBrowser ----
    private String info(Item it) { return it.getRarity().getLabel() + " - " + it.getDescription(); }

    private List<Entry> inventoryEntries() {
        List<Entry> out = new ArrayList<>();
        for (InventorySlot sl : s.inventory().list(player)) {
            Item it = sl.getItem();
            out.add(new Entry(it.getName(), it.getCategory(), info(it), Theme.rarity(it.getRarity()),
                    player.isEquipped(it.getName()) ? "Terpasang" : "", "x" + sl.getQuantity(), it instanceof Usable ? "Pakai" : "", true,
                    () -> { toast(s.inventory().useItem(player, it)); reload(); }));
        }
        return out;
    }

    private List<Entry> buyEntries() {
        List<Entry> out = new ArrayList<>();
        for (Item it : s.shop().catalog())
            out.add(new Entry(it.getName(), it.getCategory(), info(it), Theme.rarity(it.getRarity()), "", it.getPrice() + " gold", "Beli",
                    player.getGold() >= it.getPrice(), () -> { toast(s.shop().buyItem(player, it.getName(), 1)); reload(); }));
        return out;
    }

    private List<Entry> sellEntries() {
        List<Entry> out = new ArrayList<>();
        for (InventorySlot sl : s.inventory().list(player)) {
            Item it = sl.getItem();
            out.add(new Entry(it.getName(), it.getCategory(), "Dimiliki x" + sl.getQuantity(), Theme.rarity(it.getRarity()),
                    player.isEquipped(it.getName()) ? "Terpasang" : "", s.shop().sellPrice(it) + " gold", "Jual", true,
                    () -> { toast(s.shop().sellItem(player, it.getName(), 1)); reload(); }));
        }
        return out;
    }

    private List<Entry> craftEntries() {
        Map<String, Integer> owned = new HashMap<>();
        s.inventory().list(player).forEach(sl -> owned.put(sl.getItem().getName(), sl.getQuantity()));
        List<Entry> out = new ArrayList<>();
        for (Crafting.Recipe r : s.crafting().recipes()) {
            Item it = s.items().findByName(r.result()).orElse(null);
            if (it == null) continue;
            boolean can = true; StringBuilder mats = new StringBuilder();
            for (Map.Entry<String, Integer> e : r.ingredients().entrySet()) {
                int have = owned.getOrDefault(e.getKey(), 0);
                if (have < e.getValue()) can = false;
                mats.append(mats.length() == 0 ? "" : ", ").append(e.getKey()).append(' ').append(have).append('/').append(e.getValue());
            }
            out.add(new Entry(it.getName(), it.getCategory(), it.getDescription() + "<br>Bahan: " + mats, Theme.rarity(it.getRarity()),
                    "", "", "Craft", can, () -> { toast(s.crafting().craftItem(player, r.result())); reload(); }));
        }
        return out;
    }

    // ---- aksi dan penyegaran ----
    /** Berapa detik untuk memulihkan 1 stamina saat Rest. Ubah angka ini untuk mengatur kecepatan. */
    public static final int REST_SECONDS_PER_STAMINA = 2;
    private javax.swing.Timer restTimer;
    private boolean resting;
    private Theme.Log restLog;
    private HomePage home;
    private boolean busy;

    public void run(Activity a, Theme.Log log) {
        if (resting) { toast("Hentikan istirahat dulu (klik Berhenti)."); return; }
        if (busy) { toast("Tunggu petualangan selesai..."); return; }
        try {
            ActivityResult r = s.game().perform(player, a);
            busy = true;
            log.play(a.getName(), r.getLogs(), summary(r), () -> { busy = false; reload(); });
        } catch (Exception ex) { busy = false; error(ex); }
    }

    /** Hadiah ditampilkan setelah adegan selesai. Kosong kalau tidak ada hadiah (misalnya stamina kurang). */
    private List<String> summary(ActivityResult r) {
        List<String> out = new ArrayList<>();
        if (r.getExp() == 0 && r.getGold() == 0 && r.getLoot().isEmpty()) return out;
        out.add("+" + r.getExp() + " EXP, +" + r.getGold() + " gold");
        Map<String, Integer> count = new LinkedHashMap<>();
        r.getLoot().forEach(i -> count.merge(i.getName(), 1, Integer::sum));
        out.add(count.isEmpty() ? "Item: tidak ada" : "Item: " + count.entrySet().stream()
                .map(e -> e.getKey() + " x" + e.getValue()).collect(java.util.stream.Collectors.joining(", ")));
        if (r.isLeveledUp()) out.add("LEVEL UP! Sekarang level " + player.getLevel());
        return out;
    }

    /** Rest bertahap: +1 stamina tiap REST_SECONDS_PER_STAMINA detik. Klik lagi untuk berhenti. */
    public void rest(Theme.Log log) {
        if (busy) { toast("Tunggu petualangan selesai..."); return; }
        if (resting) { stopRest("Kamu berhenti beristirahat."); return; }
        if (player.getStamina() >= player.getMaxStamina() && player.getHp() >= player.getMaxHp()) {
            toast("Kamu sudah segar, tidak perlu istirahat."); return;
        }
        resting = true; restLog = log;
        log.add("Istirahat", List.of("Kamu duduk bersandar dan memejamkan mata... (+1 stamina tiap "
                + REST_SECONDS_PER_STAMINA + " detik)"));
        home.setResting(true, restInfo());
        restTimer.start();
    }

    private void restTick() {
        player.restoreStamina(1);
        player.heal(Math.max(1, player.getMaxHp() / 50));
        s.players().save(player);
        updateBars();
        boolean full = player.getStamina() >= player.getMaxStamina() && player.getHp() >= player.getMaxHp();
        if (full) stopRest("Kamu merasa segar kembali!"); else home.setResting(true, restInfo());
    }

    private void stopRest(String message) {
        restTimer.stop(); resting = false;
        restLog.add("Selesai istirahat", List.of(message, restInfo() + "  |  HP " + player.getHp() + "/" + player.getMaxHp()));
        home.setResting(false, "");
    }

    private String restInfo() { return "STA " + player.getStamina() + "/" + player.getMaxStamina(); }

    public void toast(String msg) { toast.setText(msg); toastTimer.restart(); }

    private void error(Exception ex) { JOptionPane.showMessageDialog(this, String.valueOf(ex.getMessage()), "Terjadi kesalahan", JOptionPane.ERROR_MESSAGE); }

    private void updateBars() {
        hp.set(player.getHp(), player.getMaxHp()); hpText.setText(player.getHp() + "/" + player.getMaxHp());
        sta.set(player.getStamina(), player.getMaxStamina()); staText.setText(player.getStamina() + "/" + player.getMaxStamina());
        goldText.setText(player.getGold() + " gold");
    }

    public void reload() {
        player = s.players().findById(player.getId()).orElseThrow();
        updateBars(); who.setText(player.getName()); lvl.setText("Level " + player.getLevel());
        expBar.set(player.getExp(), player.expToNextLevel()); expText.setText("EXP " + player.getExp() + " / " + player.expToNextLevel());
        welcome.setText("Selamat datang, " + player.getName());
        map.values().forEach(Page::refresh);
    }

    private void show(String key) {
        if (busy) return;
        cards.show(pages, key);
        nav.forEach((k, b) -> b.setBase(k.equals(key) ? new Color(0x232838) : Theme.SIDE));
    }
}
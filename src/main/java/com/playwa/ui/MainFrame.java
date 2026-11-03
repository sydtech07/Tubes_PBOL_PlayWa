package com.playwa.ui;

import com.playwa.activity.ActivityResult;
import com.playwa.activity.Adventure;
import com.playwa.activity.Hunting;
import com.playwa.model.Player;
import com.playwa.model.Item;
import com.playwa.model.InventorySlot;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.GameService;
import com.playwa.service.Inventory;
import com.playwa.service.Shop;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private Player player;

    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final Inventory inventoryService;
    private final Shop shopService;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JPanel dashboardPanel;
    private JPanel characterPanel;
    private JPanel inventoryPanel;
    private JPanel shopPanel;

    private JTextArea exploreLog;
    private JTextArea huntingLog;

    private final Color DARK = new Color(18, 21, 31);
    private final Color SIDEBAR = new Color(24, 27, 38);
    private final Color PANEL = new Color(35, 40, 55);
    private final Color ACCENT = new Color(92, 103, 242);

    public MainFrame(
            Player player,
            GameService gameService,
            PlayerRepository playerRepository,
            Inventory inventoryService,
            Shop shopService
    ) {

        this.player = player;
        this.gameService = gameService;
        this.playerRepository = playerRepository;
        this.inventoryService = inventoryService;
        this.shopService = shopService;

        setTitle("Play W.A");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createContent(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        JLabel logo = new JLabel("PLAY W.A");

        logo.setFont(new Font("Arial", Font.BOLD, 25));
        logo.setForeground(Color.WHITE);
        logo.setBorder(
                BorderFactory.createEmptyBorder(25, 20, 30, 10)
        );

        sidebar.add(logo);

        String[] menus = {
                "Dashboard",
                "Explore",
                "Hunting",
                "Battle",
                "Dungeon",
                "Inventory",
                "Shop",
                "Crafting",
                "Character"
        };

        for (String menu : menus) {

            JButton button = new JButton(menu);

            button.setMaximumSize(new Dimension(220, 45));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(35, 40, 55));
            button.setFocusPainted(false);
            button.setBorderPainted(false);

            button.addActionListener(e -> showPage(menu));

            sidebar.add(button);
        }

        return sidebar;
    }

    private JPanel createContent() {

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        dashboardPanel = createDashboard();
        characterPanel = createCharacterPage();
        inventoryPanel = createInventoryPage();
        shopPanel = createShopPage();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(createExplorePage(), "Explore");
        contentPanel.add(createHuntingPage(), "Hunting");

        contentPanel.add(
                createSimplePage(
                        "BATTLE",
                        "Hadapi monster dalam pertarungan."
                ),
                "Battle"
        );

        contentPanel.add(
                createSimplePage(
                        "DUNGEON",
                        "Jelajahi dungeon dan hadapi tantangan."
                ),
                "Dungeon"
        );

        contentPanel.add(inventoryPanel, "Inventory");
        contentPanel.add(shopPanel, "Shop");

        contentPanel.add(
                createSimplePage(
                        "CRAFTING",
                        "Buat item menggunakan material."
                ),
                "Crafting"
        );

        contentPanel.add(characterPanel, "Character");

        return contentPanel;
    }

    private JPanel createDashboard() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK);

        JPanel infoPanel = new JPanel();

        infoPanel.setBackground(PANEL);
        infoPanel.setLayout(
                new BoxLayout(infoPanel, BoxLayout.Y_AXIS)
        );

        infoPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JLabel welcome = new JLabel(
                "Welcome, " + player.getName() + "!"
        );

        welcome.setFont(new Font("Arial", Font.BOLD, 28));
        welcome.setForeground(Color.WHITE);

        infoPanel.add(welcome);
        infoPanel.add(Box.createVerticalStrut(25));

        addStat(infoPanel, "Level", player.getLevel());
        addStat(infoPanel, "EXP", player.getExp());
        addStat(infoPanel, "Gold", player.getGold());

        addTextStat(
                infoPanel,
                "HP",
                player.getHp() + " / " + player.getMaxHp()
        );

        addTextStat(
                infoPanel,
                "Stamina",
                player.getStamina() + " / " + player.getMaxStamina()
        );

        addStat(infoPanel, "Attack", player.getTotalAttack());
        addStat(infoPanel, "Defense", player.getTotalDefense());

        panel.add(infoPanel);

        return panel;
    }

    private JPanel createCharacterPage() {

        JPanel panel = createActivityPanel(
                "CHARACTER",
                "Informasi dan status karakter milikmu."
        );

        JPanel infoPanel = new JPanel();

        infoPanel.setBackground(PANEL);
        infoPanel.setLayout(
                new BoxLayout(infoPanel, BoxLayout.Y_AXIS)
        );

        infoPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel name = new JLabel(
                "Nama Karakter: " + player.getName()
        );

        name.setForeground(Color.WHITE);
        name.setFont(new Font("Arial", Font.BOLD, 20));

        infoPanel.add(name);
        infoPanel.add(Box.createVerticalStrut(20));

        addStat(infoPanel, "Level", player.getLevel());
        addStat(infoPanel, "EXP", player.getExp());
        addStat(infoPanel, "Gold", player.getGold());

        addTextStat(
                infoPanel,
                "HP",
                player.getHp() + " / " + player.getMaxHp()
        );

        addTextStat(
                infoPanel,
                "Stamina",
                player.getStamina() + " / " + player.getMaxStamina()
        );

        addStat(infoPanel, "Attack", player.getTotalAttack());
        addStat(infoPanel, "Defense", player.getTotalDefense());

        panel.add(infoPanel);

        return panel;
    }

    private JPanel createInventoryPage() {

        JPanel panel = createActivityPanel(
                "INVENTORY",
                "Lihat dan gunakan item milik karaktermu."
        );

        JPanel itemList = new JPanel();

        itemList.setBackground(DARK);
        itemList.setLayout(
                new BoxLayout(itemList, BoxLayout.Y_AXIS)
        );

        List<InventorySlot> slots =
                inventoryService.list(player);

        if (slots.isEmpty()) {

            JLabel emptyLabel = new JLabel(
                    "Inventory masih kosong."
            );

            emptyLabel.setForeground(Color.LIGHT_GRAY);
            emptyLabel.setFont(
                    new Font("Arial", Font.PLAIN, 16)
            );

            itemList.add(emptyLabel);

        } else {

            for (InventorySlot slot : slots) {

                itemList.add(createInventoryItem(slot));
                itemList.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemList);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DARK);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(
                new Dimension(750, 420)
        );

        panel.add(scrollPane);

        return panel;
    }

    private JPanel createInventoryItem(InventorySlot slot) {

        JPanel card = new JPanel(
                new BorderLayout(15, 5)
        );

        card.setBackground(PANEL);

        card.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        card.setMaximumSize(new Dimension(800, 150));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel information = new JPanel();

        information.setBackground(PANEL);
        information.setLayout(
                new BoxLayout(information, BoxLayout.Y_AXIS)
        );

        Item item = slot.getItem();

        JLabel name = new JLabel(item.getName());

        name.setForeground(Color.WHITE);
        name.setFont(new Font("Arial", Font.BOLD, 17));

        JLabel category = new JLabel(
                "Kategori: " + item.getCategory()
        );

        JLabel rarity = new JLabel(
                "Rarity: " + item.getRarity().name()
        );

        JLabel quantity = new JLabel(
                "Jumlah: " + slot.getQuantity()
        );

        JLabel description = new JLabel(
                "Deskripsi: " + item.getDescription()
        );

        JLabel[] labels = {
                category,
                rarity,
                quantity,
                description
        };

        for (JLabel label : labels) {

            label.setForeground(Color.LIGHT_GRAY);
            label.setFont(
                    new Font("Arial", Font.PLAIN, 13)
            );
        }

        information.add(name);
        information.add(Box.createVerticalStrut(7));

        for (JLabel label : labels) {

            information.add(label);
            information.add(Box.createVerticalStrut(3));
        }

        JButton useButton = createActionButton("GUNAKAN");

        useButton.addActionListener(e ->
                useInventoryItem(item)
        );

        card.add(information, BorderLayout.CENTER);
        card.add(useButton, BorderLayout.EAST);

        return card;
    }

    private void useInventoryItem(Item item) {

        try {

            String message = inventoryService.useItem(
                    player,
                    item
            );

            updatePlayer();

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Inventory",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cardLayout.show(contentPanel, "Inventory");

        } catch (Exception ex) {

            showError(ex);
        }
    }

    // =========================
    // SHOP PAGE
    // =========================

    private JPanel createShopPage() {

        JPanel panel = createActivityPanel(
                "SHOP",
                "Beli item menggunakan Gold atau jual item milikmu."
        );

        JLabel goldLabel = new JLabel(
                "Gold milikmu: " + player.getGold()
        );

        goldLabel.setForeground(Color.YELLOW);
        goldLabel.setFont(new Font("Arial", Font.BOLD, 17));
        goldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(goldLabel);
        panel.add(Box.createVerticalStrut(20));

        JLabel catalogTitle = new JLabel("KATALOG ITEM");

        catalogTitle.setForeground(Color.WHITE);
        catalogTitle.setFont(new Font("Arial", Font.BOLD, 20));
        catalogTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(catalogTitle);
        panel.add(Box.createVerticalStrut(10));

        JPanel catalogList = new JPanel();

        catalogList.setBackground(DARK);
        catalogList.setLayout(
                new BoxLayout(catalogList, BoxLayout.Y_AXIS)
        );

        List<Item> catalog = shopService.catalog();

        if (catalog.isEmpty()) {

            JLabel emptyLabel = new JLabel(
                    "Belum ada item yang dijual."
            );

            emptyLabel.setForeground(Color.LIGHT_GRAY);
            catalogList.add(emptyLabel);

        } else {

            for (Item item : catalog) {

                catalogList.add(createShopItem(item));
                catalogList.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane catalogScroll = new JScrollPane(catalogList);

        catalogScroll.setBorder(null);
        catalogScroll.getViewport().setBackground(DARK);
        catalogScroll.setPreferredSize(new Dimension(750, 190));
        catalogScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(catalogScroll);
        panel.add(Box.createVerticalStrut(25));

        JLabel sellTitle = new JLabel("ITEM MILIKMU");

        sellTitle.setForeground(Color.WHITE);
        sellTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sellTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(sellTitle);
        panel.add(Box.createVerticalStrut(10));

        JPanel ownedList = new JPanel();

        ownedList.setBackground(DARK);
        ownedList.setLayout(
                new BoxLayout(ownedList, BoxLayout.Y_AXIS)
        );

        List<InventorySlot> slots =
                inventoryService.list(player);

        if (slots.isEmpty()) {

            JLabel emptyLabel = new JLabel(
                    "Kamu belum memiliki item untuk dijual."
            );

            emptyLabel.setForeground(Color.LIGHT_GRAY);
            ownedList.add(emptyLabel);

        } else {

            for (InventorySlot slot : slots) {

                ownedList.add(createSellItem(slot));
                ownedList.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane ownedScroll = new JScrollPane(ownedList);

        ownedScroll.setBorder(null);
        ownedScroll.getViewport().setBackground(DARK);
        ownedScroll.setPreferredSize(new Dimension(750, 190));
        ownedScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(ownedScroll);

        return panel;
    }

    private JPanel createShopItem(Item item) {

        JPanel card = new JPanel(new BorderLayout(15, 5));

        card.setBackground(PANEL);
        card.setBorder(
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        card.setMaximumSize(new Dimension(800, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel information = new JPanel();

        information.setBackground(PANEL);
        information.setLayout(
                new BoxLayout(information, BoxLayout.Y_AXIS)
        );

        JLabel name = new JLabel(item.getName());

        name.setForeground(Color.WHITE);
        name.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel description = new JLabel(
                item.getDescription()
        );

        description.setForeground(Color.LIGHT_GRAY);

        JLabel price = new JLabel(
                "Harga: " + item.getPrice() + " Gold"
        );

        price.setForeground(Color.YELLOW);

        information.add(name);
        information.add(Box.createVerticalStrut(5));
        information.add(description);
        information.add(price);

        JButton buyButton = createActionButton("BELI");

        buyButton.addActionListener(e ->
                buyShopItem(item)
        );

        card.add(information, BorderLayout.CENTER);
        card.add(buyButton, BorderLayout.EAST);

        return card;
    }

    private JPanel createSellItem(InventorySlot slot) {

        JPanel card = new JPanel(new BorderLayout(15, 5));

        card.setBackground(PANEL);
        card.setBorder(
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        card.setMaximumSize(new Dimension(800, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        Item item = slot.getItem();

        JPanel information = new JPanel();

        information.setBackground(PANEL);
        information.setLayout(
                new BoxLayout(information, BoxLayout.Y_AXIS)
        );

        JLabel name = new JLabel(item.getName());

        name.setForeground(Color.WHITE);
        name.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel quantity = new JLabel(
                "Jumlah: " + slot.getQuantity()
        );

        quantity.setForeground(Color.LIGHT_GRAY);

        JLabel price = new JLabel(
                "Harga jual: " + shopService.sellPrice(item) + " Gold"
        );

        price.setForeground(Color.YELLOW);

        information.add(name);
        information.add(quantity);
        information.add(price);

        JButton sellButton = createActionButton("JUAL");

        sellButton.addActionListener(e ->
                sellShopItem(item)
        );

        card.add(information, BorderLayout.CENTER);
        card.add(sellButton, BorderLayout.EAST);

        return card;
    }

    private void buyShopItem(Item item) {

        String input = JOptionPane.showInputDialog(
                this,
                "Masukkan jumlah " + item.getName() + " yang ingin dibeli:",
                "Beli Item",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;
        }

        try {

            int quantity = Integer.parseInt(input.trim());

            String message = shopService.buyItem(
                    player,
                    item.getName(),
                    quantity
            );

            updatePlayer();

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Transaksi Shop",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cardLayout.show(contentPanel, "Shop");

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Masukkan jumlah dalam bentuk angka.",
                    "Input Tidak Valid",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            showError(ex);
        }
    }

    private void sellShopItem(Item item) {

        String input = JOptionPane.showInputDialog(
                this,
                "Masukkan jumlah " + item.getName() + " yang ingin dijual:",
                "Jual Item",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;
        }

        try {

            int quantity = Integer.parseInt(input.trim());

            String message = shopService.sellItem(
                    player,
                    item.getName(),
                    quantity
            );

            updatePlayer();

            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Transaksi Shop",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cardLayout.show(contentPanel, "Shop");

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Masukkan jumlah dalam bentuk angka.",
                    "Input Tidak Valid",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            showError(ex);
        }
    }

    // =========================
    // EXPLORE DAN HUNTING
    // =========================

    private JPanel createExplorePage() {

        JPanel panel = createActivityPanel(
                "EXPLORE",
                "Jelajahi dunia dan temukan berbagai sumber daya."
        );

        JButton exploreButton =
                createActionButton("MULAI EXPLORE");

        exploreLog = createLogArea();

        exploreButton.addActionListener(e -> performExplore());

        panel.add(exploreButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JScrollPane(exploreLog));

        return panel;
    }

    private JPanel createHuntingPage() {

        JPanel panel = createActivityPanel(
                "HUNTING",
                "Temui monster dan dapatkan EXP serta Gold."
        );

        JButton huntingButton =
                createActionButton("MULAI HUNTING");

        huntingLog = createLogArea();

        huntingButton.addActionListener(e -> performHunting());

        panel.add(huntingButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JScrollPane(huntingLog));

        return panel;
    }

    private JPanel createSimplePage(
            String title,
            String description
    ) {

        JPanel panel = createActivityPanel(title, description);

        JLabel information = new JLabel(
                "Fitur sedang kita integrasikan."
        );

        information.setForeground(Color.LIGHT_GRAY);
        information.setFont(
                new Font("Arial", Font.PLAIN, 15)
        );

        information.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(information);

        return panel;
    }

    // =========================
    // KOMPONEN TAMPILAN
    // =========================

    private JPanel createActivityPanel(
            String title,
            String description
    ) {

        JPanel panel = new JPanel();

        panel.setBackground(DARK);
        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(35, 40, 35, 40)
        );

        JLabel heading = new JLabel(title);

        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(description);

        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private void addStat(
            JPanel panel,
            String name,
            int value
    ) {

        addTextStat(panel, name, String.valueOf(value));
    }

    private void addTextStat(
            JPanel panel,
            String name,
            String value
    ) {

        JLabel label = new JLabel(name + ": " + value);

        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(12));
    }

    private JButton createActionButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 45));
        button.setMaximumSize(new Dimension(220, 45));

        return button;
    }

    private JTextArea createLogArea() {

        JTextArea area = new JTextArea(15, 50);

        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(PANEL);
        area.setForeground(Color.WHITE);
        area.setFont(new Font("Arial", Font.PLAIN, 14));

        area.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        return area;
    }

    // =========================
    // AKTIVITAS GAME
    // =========================

    private void performExplore() {

        try {

            ActivityResult result = gameService.perform(
                    player,
                    new Adventure()
            );

            updatePlayer();

            exploreLog.append("=== HASIL EXPLORE ===\n");

            for (String log : result.getLogs()) {
                exploreLog.append(log + "\n");
            }

            exploreLog.append(
                    "EXP diperoleh: " + result.getExp() + "\n"
            );

            exploreLog.append(
                    "Gold diperoleh: " + result.getGold() + "\n\n"
            );

            exploreLog.setCaretPosition(
                    exploreLog.getDocument().getLength()
            );

        } catch (Exception ex) {

            showError(ex);
        }
    }

    private void performHunting() {

        try {

            ActivityResult result = gameService.perform(
                    player,
                    new Hunting()
            );

            updatePlayer();

            huntingLog.append("=== HASIL HUNTING ===\n");

            for (String log : result.getLogs()) {
                huntingLog.append(log + "\n");
            }

            huntingLog.append(
                    "EXP diperoleh: " + result.getExp() + "\n"
            );

            huntingLog.append(
                    "Gold diperoleh: " + result.getGold() + "\n\n"
            );

            huntingLog.setCaretPosition(
                    huntingLog.getDocument().getLength()
            );

        } catch (Exception ex) {

            showError(ex);
        }
    }

    // =========================
    // REFRESH DATA
    // =========================

    private void updatePlayer() {

        player = playerRepository.findById(player.getId())
                .orElseThrow();

        contentPanel.remove(dashboardPanel);
        contentPanel.remove(characterPanel);
        contentPanel.remove(inventoryPanel);
        contentPanel.remove(shopPanel);

        dashboardPanel = createDashboard();
        characterPanel = createCharacterPage();
        inventoryPanel = createInventoryPage();
        shopPanel = createShopPage();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(characterPanel, "Character");
        contentPanel.add(inventoryPanel, "Inventory");
        contentPanel.add(shopPanel, "Shop");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateInventoryPage() {

        contentPanel.remove(inventoryPanel);

        inventoryPanel = createInventoryPage();

        contentPanel.add(inventoryPanel, "Inventory");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateShopPage() {

        contentPanel.remove(shopPanel);

        shopPanel = createShopPage();

        contentPanel.add(shopPanel, "Shop");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showError(Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                "Terjadi kesalahan: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showPage(String page) {

        if (page.equals("Inventory")) {
            updateInventoryPage();
        }

        if (page.equals("Shop")) {
            updateShopPage();
        }

        cardLayout.show(contentPanel, page);
    }
}
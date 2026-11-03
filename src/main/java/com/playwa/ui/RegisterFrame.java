package com.playwa.ui;

import com.playwa.model.Player;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.AuthService;
import com.playwa.service.GameService;
import com.playwa.service.Inventory;
import com.playwa.service.Shop;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final Inventory inventoryService;
    private final Shop shopService;

    private JTextField usernameField;
    private JTextField characterField;
    private JPasswordField passwordField;

    public RegisterFrame(
            AuthService authService,
            GameService gameService,
            PlayerRepository playerRepository,
            Inventory inventoryService,
            Shop shopService
    ) {

        this.authService = authService;
        this.gameService = gameService;
        this.playerRepository = playerRepository;
        this.inventoryService = inventoryService;
        this.shopService = shopService;

        setTitle("Play W.A - Register");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(24, 27, 38));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel title = new JLabel(
                "CREATE ACCOUNT",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE);

        gbc.gridy = 0;
        panel.add(title, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(
                BorderFactory.createTitledBorder("Username")
        );

        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(
                BorderFactory.createTitledBorder("Password")
        );

        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        characterField = new JTextField();
        characterField.setPreferredSize(new Dimension(300, 40));
        characterField.setBorder(
                BorderFactory.createTitledBorder("Character Name")
        );

        gbc.gridy = 3;
        panel.add(characterField, gbc);

        JButton registerButton = new JButton("REGISTER");

        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.setBackground(new Color(92, 103, 242));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        registerButton.addActionListener(e -> register());

        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        add(panel);
    }

    private void register() {

        String username = usernameField.getText();

        String password = new String(
                passwordField.getPassword()
        );

        String characterName = characterField.getText();

        try {

            Player player = authService.register(
                    username,
                    password,
                    characterName
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Akun berhasil dibuat!",
                    "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );

            MainFrame mainFrame = new MainFrame(
                    player,
                    gameService,
                    playerRepository,
                    inventoryService,
                    shopService
            );

            mainFrame.setVisible(true);
            dispose();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Registrasi Gagal",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Terjadi kesalahan: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
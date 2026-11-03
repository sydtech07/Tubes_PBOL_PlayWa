package com.playwa.ui;

import com.playwa.model.Player;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.AuthService;
import com.playwa.service.GameService;
import com.playwa.service.Inventory;
import com.playwa.service.Shop;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final GameService gameService;
    private final PlayerRepository playerRepository;
    private final Inventory inventoryService;
    private final Shop shopService;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(
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

        setTitle("Play W.A - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(24, 27, 38));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel title = new JLabel(
                "PLAY W.A",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        gbc.gridy = 0;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel(
                "Welcome Back, Adventurer!",
                SwingConstants.CENTER
        );

        subtitle.setForeground(Color.LIGHT_GRAY);

        gbc.gridy = 1;
        panel.add(subtitle, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBorder(
                BorderFactory.createTitledBorder("Username")
        );

        gbc.gridy = 2;
        panel.add(usernameField, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBorder(
                BorderFactory.createTitledBorder("Password")
        );

        gbc.gridy = 3;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("LOGIN");

        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBackground(new Color(92, 103, 242));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        loginButton.addActionListener(e -> login());

        gbc.gridy = 4;
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Create Account");

        registerButton.setForeground(Color.WHITE);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);

        registerButton.addActionListener(e -> {

            RegisterFrame registerFrame = new RegisterFrame(
                    authService,
                    gameService,
                    playerRepository,
                    inventoryService,
                    shopService
            );

            registerFrame.setVisible(true);
        });

        gbc.gridy = 5;
        panel.add(registerButton, gbc);

        add(panel);
    }

    private void login() {

        String username = usernameField.getText();

        String password = new String(
                passwordField.getPassword()
        );

        try {

            Optional<Player> result =
                    authService.login(username, password);

            if (result.isPresent()) {

                Player player = result.get();

                JOptionPane.showMessageDialog(
                        this,
                        "Login berhasil! Selamat datang.",
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

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Username atau password salah.",
                        "Login Gagal",
                        JOptionPane.ERROR_MESSAGE
                );
            }

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
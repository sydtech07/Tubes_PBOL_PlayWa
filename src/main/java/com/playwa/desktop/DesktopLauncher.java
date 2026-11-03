package com.playwa.desktop;

import com.playwa.PlayWaApplication;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.AuthService;
import com.playwa.service.GameService;
import com.playwa.service.Inventory;
import com.playwa.service.Shop;
import com.playwa.ui.LoginFrame;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

public class DesktopLauncher {

    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");

        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalStateException(
                    "Java berjalan dalam mode Headless."
            );
        }

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(PlayWaApplication.class)
                        .web(WebApplicationType.NONE)
                        .headless(false)
                        .run(args);

        AuthService authService =
                context.getBean(AuthService.class);

        GameService gameService =
                context.getBean(GameService.class);

        PlayerRepository playerRepository =
                context.getBean(PlayerRepository.class);

        Inventory inventoryService =
                context.getBean(Inventory.class);

        Shop shopService =
                context.getBean(Shop.class);

        SwingUtilities.invokeLater(() -> {

            new LoginFrame(
                    authService,
                    gameService,
                    playerRepository,
                    inventoryService,
                    shopService
            ).setVisible(true);

        });
    }
}
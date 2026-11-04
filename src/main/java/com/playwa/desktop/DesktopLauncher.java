package com.playwa.desktop;

import com.playwa.PlayWaApplication;
import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.*;
import com.playwa.ui.AuthFrame;
import com.playwa.ui.Services;
import com.playwa.ui.Theme;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.SwingUtilities;

/** Titik masuk game desktop (Swing). Jalankan class ini. */
public class DesktopLauncher {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(PlayWaApplication.class)
            .web(WebApplicationType.NONE).headless(false).run(args);

        Services services = new Services(ctx.getBean(AuthService.class), ctx.getBean(GameService.class),
            ctx.getBean(PlayerRepository.class), ctx.getBean(ItemRepository.class), ctx.getBean(Inventory.class),
            ctx.getBean(Shop.class), ctx.getBean(Crafting.class));

        SwingUtilities.invokeLater(() -> { Theme.install(); new AuthFrame(services).setVisible(true); });
    }
}

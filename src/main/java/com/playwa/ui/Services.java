package com.playwa.ui;

import com.playwa.repository.ItemRepository;
import com.playwa.repository.PlayerRepository;
import com.playwa.service.*;

/** Kumpulan service yang dipakai UI (dibuat sekali oleh DesktopLauncher). */
public record Services(AuthService auth, GameService game, PlayerRepository players, ItemRepository items,
                       Inventory inventory, Shop shop, Crafting crafting) {}

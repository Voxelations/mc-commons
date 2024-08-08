package com.voxelations.common.platform.paper.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import fr.minuskube.inv.InventoryManager;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@AllArgsConstructor
public class CommonPaperModule extends AbstractModule {

    private final JavaPlugin plugin;

    @Provides
    @Singleton
    public InventoryManager provideInventoryManager() {
        InventoryManager inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();

        return inventoryManager;
    }
}

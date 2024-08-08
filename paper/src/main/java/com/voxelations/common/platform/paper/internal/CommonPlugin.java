package com.voxelations.common.platform.paper.internal;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.voxelations.common.CommonModule;
import com.voxelations.common.config.ConfigService;
import com.voxelations.common.config.internal.SQLConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

@Getter
@ApiStatus.Internal
public class CommonPlugin extends JavaPlugin {

    private final Injector injector = Guice.createInjector(new CommonModule(), new CommonPaperModule(this));

    @Override
    public void onEnable() {
        // Enable any registrable services provided by commons
        ConfigService configService = injector.getInstance(ConfigService.class);
        configService.enable();
        configService.register(getDataPath(), SQLConfig.class);
    }

    @Override
    public void onDisable() {
        // Disable any registrable services provided by commons
        injector.getInstance(ConfigService.class).disable();
    }
}

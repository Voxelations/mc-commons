package com.voxelations.common.platform.paper;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.voxelations.common.platform.paper.internal.CommonPlugin;
import com.voxelations.common.registrar.PluginMetadata;
import com.voxelations.common.registrar.Registrable;
import com.voxelations.common.registrar.Registrar;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.nio.file.Path;

@Getter
public abstract class PaperPlugin extends JavaPlugin implements Registrable, PluginMetadata {

    protected final Injector injector;

    public PaperPlugin(AbstractModule... modules) {
        // Append in the plugin's default module
        AbstractModule[] allModules = new AbstractModule[modules.length + 1];
        allModules[0] = new PaperPluginModule(this);
        System.arraycopy(modules, 0, allModules, 1, modules.length);

        // Create the injector
        this.injector = getPlugin(CommonPlugin.class).getInjector().createChildInjector(allModules);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onEnable() {
        injector.getInstance(Registrar.class).enable();

        enable();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void onDisable() {
        injector.getInstance(Registrar.class).disable();

        disable();
    }

    @Override
    public Path getDataDirectory() {
        return getDataPath();
    }
}

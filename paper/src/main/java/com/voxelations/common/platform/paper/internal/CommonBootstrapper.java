package com.voxelations.common.platform.paper.internal;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.Internal
public class CommonBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        // no-op - just here if another plugin depends on this one at bootstrapping
    }
}

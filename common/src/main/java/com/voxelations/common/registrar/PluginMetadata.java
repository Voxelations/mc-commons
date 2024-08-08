package com.voxelations.common.registrar;

import java.nio.file.Path;

public interface PluginMetadata {

    /**
     * @return the data directory
     */
    Path getDataDirectory();
}

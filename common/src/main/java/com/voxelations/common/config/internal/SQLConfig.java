package com.voxelations.common.config.internal;

import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Data
public class SQLConfig {

    private final String jdbcUrl = "jdbc:sqlite:./plugins/mc-commons/database.db";
    private final String username = "";
    private final String password = "";
}

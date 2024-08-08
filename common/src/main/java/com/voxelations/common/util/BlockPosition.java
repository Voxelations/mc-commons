package com.voxelations.common.util;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record BlockPosition(String world, int x, int y, int z) {
}

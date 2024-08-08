package com.voxelations.common.util;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record FinePosition(String world, double x, double y, double z, float yaw, float pitch) {
}

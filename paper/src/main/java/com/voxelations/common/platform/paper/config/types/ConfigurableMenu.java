package com.voxelations.common.platform.paper.config.types;

import lombok.Builder;
import org.bukkit.event.inventory.InventoryType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@Builder
@ConfigSerializable
public record ConfigurableMenu(String title, int rows, InventoryType type, Map<String, Drawable> items, @Nullable Paged paged) {

    @ConfigSerializable
    public record Drawable(List<Integer> slots, ConfigurableItemStack item) {
    }

    @Builder
    @ConfigSerializable
    public record Paged(int firstSlot, int elementsPerPage, @Nullable List<Integer> blacklistedSlots, Drawable previousPage, Drawable nextPage, ConfigurableItemStack template) {
    }
}

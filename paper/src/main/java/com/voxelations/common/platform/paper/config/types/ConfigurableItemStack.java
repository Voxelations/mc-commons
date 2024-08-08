package com.voxelations.common.platform.paper.config.types;

import com.voxelations.common.platform.paper.util.ItemStacks;
import com.voxelations.common.util.AdventureUtil;
import lombok.Builder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Builder
@ConfigSerializable
public record ConfigurableItemStack(
        @Nullable Material type,
        @Nullable String snbt,
        @Nullable Integer amount,
        @Nullable String displayName,
        @Nullable List<String> lore,
        @Nullable Integer customModelData,
        @Nullable String skullOwner,
        @Nullable String skullTexture
) {

    public ItemStack toItemStack(TagResolver... placeholders) {
        if (type == null && snbt == null) throw new IllegalStateException("Either `type` or `snbt` must be set");
        if (snbt != null) return Bukkit.getItemFactory().createItemStack(snbt);

        return ItemStacks.builder()
                .from(ItemStack.of(Objects.requireNonNull(type)))
                .amount(amount)
                .displayName(Optional.ofNullable(displayName).map(it -> AdventureUtil.toComponent(it, placeholders)).orElse(null))
                .lore(Optional.ofNullable(lore).map(it -> AdventureUtil.toComponent(it, placeholders)).orElse(null))
                .customModelData(customModelData)
                .skullOwner(skullOwner)
                .skullTexture(skullTexture)
                .build();
    }
}

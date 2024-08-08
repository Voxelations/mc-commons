package com.voxelations.common.platform.paper.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.Builder;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;

@UtilityClass
public class ItemStacks {

    private static final UUID NIL = new UUID(0, 0);

    @Builder
    private ItemStack $builder(
            ItemStack from,
            @Nullable Integer amount,
            @Nullable Component displayName,
            @Nullable List<Component> lore,
            @Nullable Integer customModelData,
            @Nullable String skullOwner,
            @Nullable String skullTexture
    ) {
        ItemStack item = from.clone();

        if (amount != null) item.setAmount(amount);
        if (displayName != null) item.editMeta(meta -> meta.displayName(displayName));
        if (lore != null) item.editMeta(meta -> meta.lore(lore));
        if (customModelData != null) item.editMeta(meta -> meta.setCustomModelData(customModelData));
        if (skullOwner != null) item.editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner)));
        if (skullTexture != null) item.editMeta(SkullMeta.class, meta -> {
            PlayerProfile pp = Bukkit.createProfile(NIL);
            pp.setProperty(new ProfileProperty("textures", skullTexture));

            meta.setPlayerProfile(pp);
        });

        return item;
    }
}

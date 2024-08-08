package com.voxelations.common.platform.paper.invs;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.voxelations.common.platform.paper.config.types.ConfigurableMenu;
import com.voxelations.common.util.AdventureUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import fr.minuskube.inv.content.SlotPos;
import lombok.Builder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MenuFactory {

    private final Provider<InventoryManager> inventoryManager;
    private static final BiFunction<String, ConfigurableMenu.Drawable, ItemStack> NIL_PRE_PROCESSOR = (id, drawable) -> drawable.item().toItemStack();
    private static final BiConsumer<String, InventoryClickEvent> NIL_CLICK_HANDLER = (id, click) -> {};
    private static final TagResolver[] NIL_PLACEHOLDERS = new TagResolver[0];

    @Inject
    public MenuFactory(Provider<InventoryManager> inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Builder
    private SmartInventory.Builder $builder(
            ConfigurableMenu configurableMenu,
            BiFunction<String, ConfigurableMenu.Drawable, ItemStack> preProcessor,
            BiConsumer<String, InventoryClickEvent> clickHandler,
            @Nullable ClickableItem[] pageElements,
            @Nullable TagResolver[] placeholders
    ) {
        return SmartInventory.builder()
                .manager(inventoryManager.get())
                .id(configurableMenu.title())
                .title(AdventureUtil.toComponent(configurableMenu.title(), Objects.requireNonNullElse(placeholders, NIL_PLACEHOLDERS)))
                .size(configurableMenu.rows(), 9)
                .type(configurableMenu.type())
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        // Fill in the drawables
                        configurableMenu.items().forEach((id, drawable) ->
                                drawable.slots().stream().filter(slot -> slot != -1).forEach(slot ->
                                        contents.set(
                                                SlotPos.of(slot / 9, slot % 9),
                                                ClickableItem.of(
                                                        Objects.requireNonNullElse(preProcessor, NIL_PRE_PROCESSOR).apply(id, drawable),
                                                        e -> Objects.requireNonNullElse(clickHandler, NIL_CLICK_HANDLER).accept(id, e)
                                                )
                                        )
                                )
                        );

                        // And pagination, if applicable
                        ConfigurableMenu.Paged paged = configurableMenu.paged();
                        if (paged == null || pageElements == null) return;

                        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(paged.firstSlot() / 9, paged.firstSlot() % 9)).allowOverride(false);
                        Optional.ofNullable(paged.blacklistedSlots()).ifPresent(blacklistedSlots -> blacklistedSlots.forEach(slot -> iterator.blacklist(SlotPos.of(slot / 9, slot % 9))));
                        Pagination pagination = contents.pagination()
                                .setItemsPerPage(paged.elementsPerPage())
                                .setItems(pageElements)
                                .addToIterator(iterator);

                        if (!pagination.isFirst()) {
                            paged.previousPage().slots().forEach(slot ->
                                    contents.set(
                                            SlotPos.of(slot / 9, slot % 9),
                                            ClickableItem.of(
                                                    paged.previousPage().item().toItemStack(),
                                                    e -> inventoryManager.get().getInventory(((Player) e.getWhoClicked())).ifPresent(inv -> inv.open(((Player) e.getWhoClicked()), pagination.previous().getPage()))
                                            )
                                    )
                            );
                        }

                        if (!pagination.isLast()) {
                            paged.nextPage().slots().forEach(slot ->
                                    contents.set(
                                            SlotPos.of(slot / 9, slot % 9),
                                            ClickableItem.of(
                                                    paged.nextPage().item().toItemStack(Objects.requireNonNullElse(placeholders, NIL_PLACEHOLDERS)),
                                                    e -> inventoryManager.get().getInventory(((Player) e.getWhoClicked())).ifPresent(inv -> inv.open(((Player) e.getWhoClicked()), pagination.next().getPage()))
                                            )
                                    )
                            );
                        }
                    }

                    @Override
                    public void update(Player player, InventoryContents contents) {
                        // no-op
                    }
                });
    }
}

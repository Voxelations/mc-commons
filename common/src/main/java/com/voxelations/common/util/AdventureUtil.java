package com.voxelations.common.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

@UtilityClass
public class AdventureUtil {

    public final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Converts a component to a mini message string.
     *
     * @param component The component
     * @return The mini message string
     */
    public String toString(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Converts a mini message string to a component.
     *
     * @param text The mini message string
     * @return The component
     */
    public Component toComponent(String text, TagResolver... placeholders) {
        return MINI_MESSAGE.deserialize(text, placeholders).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a list of mini message strings to a list of components.
     *
     * @param text The mini message strings
     * @return The components
     */
    public List<Component> toComponent(List<String> text, TagResolver... placeholders) {
        return text.stream().map(it -> toComponent(it, placeholders)).toList();
    }
}

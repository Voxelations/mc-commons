package com.voxelations.common.config.types;

import com.voxelations.common.util.AdventureUtil;
import lombok.Builder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@Builder
@ConfigSerializable
public record ConfigurableMessage(@Nullable String message, @Nullable String actionBar, @Nullable Sound sound, @Nullable Title title) {

    /**
     * {@link net.kyori.adventure.title.Title} but with string parts for convenience.
     */
    @Builder
    @ConfigSerializable
    public record Title(@Nullable String title, @Nullable String subtitle, net.kyori.adventure.title.Title.Times times) {
    }

    /**
     * Replaces all given string placeholders.
     *
     * @param replacements the replacements
     * @return the new message with the replacements applied
     */
    public ConfigurableMessage withReplacements(Map<String, String> replacements) {
        // Make a new message with the string replacements applied
        ConfigurableMessageBuilder builder = ConfigurableMessage.builder();

        if (message != null) {
            String localMessage = message;
            for (Map.Entry<String, String> entry : replacements.entrySet())
                localMessage = localMessage.replace(entry.getKey(), entry.getValue());

            builder.message(localMessage);
        }

        if (actionBar != null) {
            String localActionBar = actionBar;
            for (Map.Entry<String, String> entry : replacements.entrySet())
                localActionBar = localActionBar.replace(entry.getKey(), entry.getValue());

            builder.message(localActionBar);
        }

        if (title != null) {
            String titleTitle = title.title();
            String titleSubtitle = title.subtitle();

            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (titleTitle != null) titleTitle = titleTitle.replace(key, value);
                if (titleSubtitle != null) titleSubtitle = titleSubtitle.replace(key, value);
            }

            builder.title(Title.builder().title(titleTitle).subtitle(titleSubtitle).times(title.times()).build());
        }

        return builder.build();
    }

    /**
     * Sends the message to the given audience.
     *
     * @param receiver the receiver
     * @param placeholders the placeholders
     */
    public void send(Audience receiver, TagResolver... placeholders) {
        if (message != null) receiver.sendMessage(AdventureUtil.toComponent(message, placeholders));
        if (actionBar != null) receiver.sendActionBar(AdventureUtil.toComponent(actionBar, placeholders));
        if (sound != null) receiver.playSound(sound);
        if (title != null) receiver.showTitle(net.kyori.adventure.title.Title.title(AdventureUtil.toComponent(title.title(), placeholders), AdventureUtil.toComponent(title.subtitle(), placeholders), title.times()));
    }
}

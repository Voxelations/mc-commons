package com.voxelations.common.config.types;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationSerializer extends ScalarSerializer<Duration> {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhms])");

    public DurationSerializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(Type type, Object obj) throws SerializationException {
        String input = obj.toString();
        Matcher matcher = DURATION_PATTERN.matcher(input);

        Duration duration = Duration.ZERO;
        while (matcher.find()) {
            String group = matcher.group();
            String timeUnit = String.valueOf(group.charAt(group.length() - 1));
            int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));

            switch (timeUnit) {
                case "d" -> duration = duration.plusDays(timeValue);
                case "h" -> duration = duration.plusHours(timeValue);
                case "m" -> duration = duration.plusMinutes(timeValue);
                case "s" -> duration = duration.plusSeconds(timeValue);
                default -> throw new SerializationException(type, "Invalid duration format: `%s` (expected format: 1d2h3m4s)".formatted(input));
            }
        }

        if (duration.isZero()) throw new SerializationException(type, "Invalid duration format: `%s` (expected format: 1d2h3m4s)".formatted(input));

        return duration;
    }

    @Override
    public Object serialize(Duration item, Predicate<Class<?>> typeSupported) {
        long days = item.toDays();
        item = item.minusDays(days);

        long hours = item.toHours();
        item = item.minusHours(hours);

        long minutes = item.toMinutes();
        item = item.minusMinutes(minutes);

        long seconds = item.getSeconds();

        StringBuilder builder = new StringBuilder();
        if (days > 0) builder.append(days).append("d");
        if (hours > 0) builder.append(hours).append("h");
        if (minutes > 0) builder.append(minutes).append("m");
        if (seconds > 0 || builder.isEmpty()) builder.append(seconds).append("s"); // Always include seconds for potential zero duration

        return builder.toString();
    }
}

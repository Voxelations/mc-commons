package com.voxelations.common.platform.paper.data;

import com.voxelations.common.data.DataEntity;
import com.voxelations.common.data.DataRepository;
import com.voxelations.common.data.DataService;
import com.voxelations.common.platform.paper.util.Schedulers;
import com.voxelations.common.registrar.Registrable;
import io.papermc.paper.util.Tick;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PlayerDataService<T extends DataEntity> implements DataService<UUID, T>, Listener, Registrable {

    @Getter
    private final Map<UUID, T> cache = new ConcurrentHashMap<>();
    @Getter
    private final DataRepository<UUID, T> repository;
    private final Schedulers schedulers;
    private final Function<UUID, T> orCreate;

    public PlayerDataService(DataRepository<UUID, T> repository, Schedulers schedulers, Function<UUID, T> orCreate) {
        this.repository = repository;
        this.schedulers = schedulers;
        this.orCreate = orCreate;
    }

    @Override
    public void enable() {
        schedulers.getAsync().runTask(0, Tick.tick().fromDuration(Duration.ofMinutes(5)), () -> {
            // Collect all the dirty entities
            List<T> dirtyEntities = new ArrayList<>();
            cache.forEach((uuid, entity) -> {
                if (entity.isDirty()) {
                    dirtyEntities.add(entity);
                    entity.setDirty(false);
                }
            });

            // And save them
            if (!dirtyEntities.isEmpty()) {
                repository.saveAll(dirtyEntities);
            }
        });
    }

    @Override
    public void disable() {
        List<T> dirtyEntities = new ArrayList<>();
        cache.forEach((uuid, entity) -> {
            if (entity.isDirty()) dirtyEntities.add(entity);
        });

        repository.saveAll(dirtyEntities);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        schedulers.getAsync().runTask(() -> cache.put(uuid, Objects.requireNonNullElse(resolve(uuid), orCreate.apply(uuid))));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        schedulers.getAsync().runTask(() -> {
            T entity = cache.remove(uuid);
            if (entity != null && entity.isDirty()) repository.save(entity);
        });
    }

    @Override
    @Nullable
    public T resolve(UUID uuid) {
        T entity = cache.get(uuid);
        if (entity != null) return entity;

        return repository.findById(uuid);
    }
}

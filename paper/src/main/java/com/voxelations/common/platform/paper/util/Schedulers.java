package com.voxelations.common.platform.paper.util;

import com.google.inject.Inject;
import com.voxelations.common.platform.paper.PaperPlugin;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

@Getter
public class Schedulers {

    @Getter(AccessLevel.NONE)
    private final PaperPlugin plugin;
    private final Scheduler sync = new Sync();
    private final Scheduler async = new Async();

    @Inject
    public Schedulers(PaperPlugin plugin) {
        this.plugin = plugin;
    }

    public sealed interface Scheduler permits Sync, Async {
        /**
         * Runs the given runnable
         *
         * @param runnable The runnable
         * @return The task
         */
        BukkitTask runTask(Runnable runnable);

        /**
         * Runs the given runnable after the given delay
         *
         * @param delay The delay
         * @param runnable The runnable
         * @return The task
         */
        BukkitTask runTask(long delay, Runnable runnable);

        /**
         * Runs the given runnable after the given delay and period
         *
         * @param delay The delay
         * @param period The period
         * @param runnable The runnable
         * @return The task
         */
        BukkitTask runTask(long delay, long period, Runnable runnable);
    }

    final class Sync implements Scheduler {

        @Override
        public BukkitTask runTask(Runnable runnable) {
            return plugin.getServer().getScheduler().runTask(plugin, runnable);
        }

        @Override
        public BukkitTask runTask(long delay, Runnable runnable) {
            return plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTask(long delay, long period, Runnable runnable) {
            return plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
        }
    }

    final class Async implements Scheduler {

        @Override
        public BukkitTask runTask(Runnable runnable) {
            return plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        }

        @Override
        public BukkitTask runTask(long delay, Runnable runnable) {
            return plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }

        @Override
        public BukkitTask runTask(long delay, long period, Runnable runnable) {
            return plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
        }
    }
}

package me.liwk.karhu.replay.view;

import me.liwk.karhu.Karhu;
import me.liwk.karhu.replay.session.ReplaySession;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ReplayViewerManager {

    private final Karhu plugin;
    private final Map<UUID, ReplayViewer> activeViewers;

    public ReplayViewerManager(Karhu plugin) {
        this.plugin = plugin;
        this.activeViewers = new ConcurrentHashMap<>();
    }

    public boolean startViewing(Player viewer, ReplaySession replay) {
        if (activeViewers.containsKey(viewer.getUniqueId())) {
            stopViewing(viewer);
        }

        ReplayViewer replayViewer = new ReplayViewer(plugin, viewer, replay);
        activeViewers.put(viewer.getUniqueId(), replayViewer);

        return replayViewer.start();
    }

    public void stopViewing(Player viewer) {
        ReplayViewer replayViewer = activeViewers.remove(viewer.getUniqueId());
        if (replayViewer != null) {
            replayViewer.stop();
        }
    }

    public boolean isViewing(Player viewer) {
        return activeViewers.containsKey(viewer.getUniqueId());
    }

    public ReplayViewer getViewer(Player viewer) {
        return activeViewers.get(viewer.getUniqueId());
    }

    public void shutdown() {
        for (ReplayViewer viewer : activeViewers.values()) {
            viewer.stop();
        }
        activeViewers.clear();
    }
}



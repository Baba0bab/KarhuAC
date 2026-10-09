package me.liwk.karhu.util.player;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import me.liwk.karhu.Karhu;

import me.liwk.karhu.handler.collision.type.MaterialChecks;
import me.liwk.karhu.util.mc.boundingbox.BoundingBox;
import me.liwk.karhu.util.task.Tasker;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.util.Vector;


import java.util.List;
import java.util.function.Consumer;

public final class BlockUtil {

    /**
     * Liquid level with the legacy data semantics (0 = source, 1-7 = flowing, 8+ = falling).
     * On 1.13+ Block#getData() goes through CraftLegacy, whose first use runs the whole
     * DataFixer and freezes the main thread for several seconds, so read the BlockData instead.
     * Waterlogged blocks and seagrass are not Levelled and count as a source.
     */
    public static int getLiquidLevel(Block block) {
        if (Karhu.SERVER_VERSION.isNewerThanOrEquals(ServerVersion.V_1_13)) {
            BlockData blockData = block.getBlockData();
            return blockData instanceof Levelled ? ((Levelled) blockData).getLevel() : 0;
        }
        return block.getData();
    }

    public static void getTileEntitiesSync(BoundingBox box, Consumer<List<Block>> listConsumer) {
        Tasker.run(() -> {
            listConsumer.accept(box.getCollidingAir());
        });
    }

    public static boolean chunkLoaded(World w, int x, int z) {
        final Location loc = new Location(w, x, 0, z);
        return chunkLoaded(loc);
    }

    public static boolean chunkLoaded(final Location loc) {
        return Karhu.getInstance().getChunkManager().isChunkLoaded(loc);
    }

    public static long getChunkPair(final Chunk chunk) {
        return (long) chunk.getX() << 32 | chunk.getZ() & 0xFFFFFFFFL;
    }

    public static long getChunkPair(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    public static long getChunkPair(final Location location) {
        return (long) (location.getBlockX() >> 4) << 32 | (location.getBlockZ() >> 4) & 0xFFFFFFFFL;
    }

    public static Vector getBlockBounds(Material material) {
        return MaterialChecks.BED.contains(material)
                ? new Vector(1F, 0.5625F, 1F)
                : MaterialChecks.FENCES.contains(material) ?
                new Vector(1F, 1.5F, 1F)
                : MaterialChecks.CLIMBABLE.contains(material) ?
                new Vector(0.8625F, 1F, 0.8625F)
                : MaterialChecks.CARPETS.contains(material) ?
                new Vector(1F, 0.0625F, 1F)
                : MaterialChecks.HALFS.contains(material) ?
                new Vector(1F, 0.5F, 1F)
                : MaterialChecks.PORTAL.contains(material) ?
                new Vector(1F, 0.8125F, 1F)
                : new Vector(1F, 1F, 1F);
    }

}

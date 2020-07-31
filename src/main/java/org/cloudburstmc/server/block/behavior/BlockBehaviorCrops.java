package org.cloudburstmc.server.block.behavior;

import com.nukkitx.math.vector.Vector3f;
import lombok.val;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockTraits;
import org.cloudburstmc.server.event.block.BlockGrowEvent;
import org.cloudburstmc.server.item.Item;
import org.cloudburstmc.server.item.ItemIds;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.particle.BoneMealParticle;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

import static org.cloudburstmc.server.block.BlockTypes.FARMLAND;

public abstract class BlockBehaviorCrops extends FloodableBlockBehavior {

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, Direction face, Vector3f clickPos, Player player) {
        if (block.down().getState().getType() == FARMLAND) {
            placeBlock(block, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(Block block, Item item, Player player) {
        //Bone meal
        if (item.getId() == ItemIds.DYE && item.getMeta() == 0x0f) {
            if (block.getState().ensureTrait(BlockTraits.GROWTH) < 7) {
                BlockGrowEvent ev = new BlockGrowEvent(block, block.getState().incrementTrait(BlockTraits.GROWTH));
                Server.getInstance().getPluginManager().callEvent(ev);

                if (ev.isCancelled()) {
                    return false;
                }

                block.set(ev.getNewState(), false);
                block.getLevel().addParticle(new BoneMealParticle(block.getPosition()));

                if (player != null && player.getGamemode().isSurvival()) {
                    item.decrementCount();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(Block block, int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (block.down().getState().getType() != FARMLAND) {
                removeBlock(block, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                val state = block.getState();
                if (state.ensureTrait(BlockTraits.GROWTH) < 0x07) {
                    BlockGrowEvent ev = new BlockGrowEvent(block, state.incrementTrait(BlockTraits.GROWTH));
                    Server.getInstance().getPluginManager().callEvent(ev);

                    if (!ev.isCancelled()) {
                        block.set(ev.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor(Block state) {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}

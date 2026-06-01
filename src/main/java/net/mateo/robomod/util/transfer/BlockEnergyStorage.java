package net.mateo.robomod.util.transfer;

import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Extends EnergyStorage with a Direction → TypeIO map so wires and batteries
 * know which sides are inputs vs outputs.
 *
 * Fabric's BlockEnergyStorage.SIDED lookup API is replaced by the static
 * findAt() helper, which just gets the BlockEntity and casts it.
 */
public abstract class BlockEnergyStorage extends EnergyStorage {

    public enum TypeIO {
        INPUT,
        OUTPUT,
        IO
    }

    // Lazily initialised — getDirectionIO() is abstract and implemented by
    // each block entity's anonymous-class override, so we cannot call it in
    // the field initialiser safely.
    private Map<Direction, TypeIO> directionIOCache;

    /**
     * Returns the direction map, building it on first access.
     * Use this instead of accessing the old Fabric directionIO field directly.
     */
    public Map<Direction, TypeIO> getDirectionIOMap() {
        if (directionIOCache == null) {
            directionIOCache = new HashMap<>();
            getDirectionIO(directionIOCache);
        }
        return directionIOCache;
    }

    /** Implemented by each block entity to declare its sided IO layout. */
    public abstract void getDirectionIO(Map<Direction, TypeIO> direction);

    // -----------------------------------------------------------------------
    // Replaces  BlockEnergyStorage.SIDED.find(world, pos, direction)
    // -----------------------------------------------------------------------

    /**
     * Looks up the BlockEnergyStorage of the block entity at pos.
     *
     * @param level         the world
     * @param pos           position to check
     * @param fromDirection the direction we are approaching from
     *                      (i.e. the opposite of the cable's outgoing direction)
     * @return the storage, or null if the block entity is not an EnergyBlockEntity
     */
    public static BlockEnergyStorage findAt(Level level, BlockPos pos, Direction fromDirection) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof EnergyBlockEntity ebe) {
            return ebe.energyStorage;
        }
        return null;
    }
}

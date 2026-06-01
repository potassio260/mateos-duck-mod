package net.mateo.robomod.util;

import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class DebugUtil {
    /**
     * FabricLoader.getInstance().isDevelopmentEnvironment()
     * → !FMLEnvironment.production  (true in dev/runClient, false in prod)
     */
    public static void updateEnergyDebug(Level world, BlockPos pos, BlockState state, EnergyBlockEntity energyBlock) {
        if (!FMLEnvironment.production) {
            energyBlock.setChanged();   // equivalent of updateListeners() — triggers block update + save
        }
    }
}

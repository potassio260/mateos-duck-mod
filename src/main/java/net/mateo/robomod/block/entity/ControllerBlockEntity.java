package net.mateo.robomod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ControllerBlockEntity extends BlockEntity {

    public ControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONTROLLER.get(), pos, state);
    }

//  public static void tick(Level level, BlockPos pos, BlockState state,
//                          ControllerBlockEntity blockEntity) {
//  }
}

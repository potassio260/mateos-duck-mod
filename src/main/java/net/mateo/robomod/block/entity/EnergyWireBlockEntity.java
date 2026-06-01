package net.mateo.robomod.block.entity;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.packet.DebugCablePacket;
import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.*;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnergyWireBlockEntity extends BlockEntity {

    public static final List<EnergyWireBlockEntity>                       wires     = new ArrayList<>();
    public static final Map<BlockEnergyStorage, List<Direction>>          storages  = new HashMap<>();
    public static final Map<BlockEnergyStorage, List<Direction>>          receivers = new HashMap<>();
    private static final Deque<EnergyWireBlockEntity>                     QueueWires = new ArrayDeque<>();
    public static int  age     = 0;
    public        int  lastage = 0;
    private EnergyWireBlockEntity ownerCable;

    public EnergyWireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_WIRE.get(), pos, state);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            age++;
        }
    }

    public void addNeighborsStorages(EnergyWireBlockEntity wire) {
        for (Direction direction : Direction.values()) {
            BlockEnergyStorage energyStorage = BlockEnergyStorage.findAt(
                    wire.getLevel(), wire.getBlockPos().relative(direction), direction.getOpposite());

            if (energyStorage != null && energyStorage.type() != null) {
                if (energyStorage.type() == IEnergyStorage.Type.RECEIVER
                        && !energyStorage.isFull()) {
                    addStorages(receivers, energyStorage, direction);
                }
                if (energyStorage.type() == IEnergyStorage.Type.BATTERY
                        || energyStorage.type() == IEnergyStorage.Type.GENERATOR) {
                    addStorages(storages, energyStorage, direction);
                }
            }
        }
    }

    private void addStorages(Map<BlockEnergyStorage, List<Direction>> map,
                             BlockEnergyStorage storage, Direction direction) {
        map.computeIfAbsent(storage, k -> new ArrayList<>())
                .add(direction.getOpposite());
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            EnergyWireBlockEntity blockEntity) {
        if (blockEntity.lastage == age) return;

        QueueWires.add(blockEntity);
        blockEntity.lastage = age;
        wires.add(blockEntity);

        while (!QueueWires.isEmpty()) {
            EnergyWireBlockEntity current = QueueWires.removeFirst();

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = current.getBlockPos().relative(direction);
                BlockEntity be = current.getLevel().getBlockEntity(neighborPos);

                if (be instanceof EnergyWireBlockEntity wire && !wires.contains(be)) {
                    if (wire.lastage != age) {
                        QueueWires.add(wire);
                        wire.lastage = age;
                        wires.add(wire);

                        if (!wire.equals(blockEntity)) {
                            wire.ownerCable = blockEntity;
                        }
                    }
                }
            }
        }

        if (wires.isEmpty()) return;

        for (EnergyWireBlockEntity wire : wires) {
            wire.addNeighborsStorages(wire);
        }

        if (!FMLEnvironment.production && level instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                HitResult hitResult = player.pick(player.getBlockReach(), 0.0F, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();

                    for (EnergyWireBlockEntity wire : wires) {
                        if (level.getBlockEntity(hitPos) instanceof EnergyWireBlockEntity cableSearch) {
                            if (cableSearch.ownerCable != null
                                    && cableSearch.ownerCable.equals(wire.ownerCable)) {
                                ModPackets.sendToPlayer(player,
                                        new DebugCablePacket(wire.getBlockPos(), false, false));

                                // FIX: wires.getFirst() is Java 21 — use wires.get(0) for Java 17
                                if (!wires.isEmpty() && wires.get(0).ownerCable == null) {
                                    ModPackets.sendToPlayer(player,
                                            new DebugCablePacket(wires.get(0).getBlockPos(), false, true));
                                }
                            }
                        }
                    }
                }
            }
        }

        blockEntity.transferEnergy(storages, receivers);
        blockEntity.transferEnergy(storages, storages);

        wires.clear();
        storages.clear();
        receivers.clear();
        QueueWires.clear();
        blockEntity.ownerCable = null;
    }

    public boolean canIO(BlockEnergyStorage storage, List<Direction> directions, boolean input) {
        Map<Direction, BlockEnergyStorage.TypeIO> ioMap = storage.getDirectionIOMap();
        for (Direction direction : directions) {
            BlockEnergyStorage.TypeIO type = ioMap.get(direction);
            if (type == null) continue;
            if (input  && (type == BlockEnergyStorage.TypeIO.INPUT  || type == BlockEnergyStorage.TypeIO.IO)) return true;
            if (!input && (type == BlockEnergyStorage.TypeIO.OUTPUT || type == BlockEnergyStorage.TypeIO.IO)) return true;
        }
        return false;
    }

    private void transferEnergy(Map<BlockEnergyStorage, List<Direction>> sources,
                                Map<BlockEnergyStorage, List<Direction>> targets) {
        if (sources.isEmpty() || targets.isEmpty()) return;

        for (var source : sources.keySet()) {
            int balancedEnergy = Math.min(source.transferRate(), source.storedEnergy) / targets.size();
            for (var target : targets.keySet()) {
                if (canIO(source, sources.get(source), false)
                        && canIO(target, targets.get(target), true)) {
                    net.mateo.robomod.util.transfer.EnergyStorage.transfer(
                            source, target,
                            // FIX: Math.clamp() is Java 21 — use Mth.clamp() from Minecraft (works on Java 17)
                            Mth.clamp(balancedEnergy, 1, source.transferRate()),
                            IEnergyStorage.Type.WIRE);
                }
            }
        }
    }
}
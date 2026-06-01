package net.mateo.robomod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import net.mateo.robomod.block.ModBlocks;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.entity.ModEntities;
import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.event.CyborgRenderEvent;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.ModCreativeModeTabs;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.network.packet.SyncCyborgStatePacket;
import net.mateo.robomod.screen.ModMenuTypes;
import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import java.util.UUID;

@Mod(RoboMod.MOD_ID)
public class RoboMod {
    public static final String MOD_ID = "robomod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final UUID CYBORG_HEALTH_UUID = UUID.fromString("a3d4e5f6-7890-1234-abcd-ef1234567890");

    public RoboMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(CyborgRenderEvent.class);
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModPackets.register();

        if (ModList.get().isLoaded("toughasnails")) {
            LOGGER.info("RoboMod: Tough As Nails detected, loading compat.");
        }

        LOGGER.info("RoboMod common setup complete.");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void onAttributeCreate(EntityAttributeCreationEvent event) {
            event.put(ModEntities.CYBORG_ENTITY.get(), CyborgEntity.createAttributes().build());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            PlayerExtension newPlayer = (PlayerExtension) event.getEntity();
            PlayerExtension oldPlayer = (PlayerExtension) event.getOriginal();

            event.getOriginal().reviveCaps();

            newPlayer.copyFrom(oldPlayer);
            if (newPlayer.isCyborg()) {
                newPlayer.setupAttributes(event.getEntity());
            }

            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp
                && sp instanceof PlayerExtension ext
                && ext.isCyborg()) {
            ModPackets.sendToPlayer(sp, new SyncCyborgStatePacket(ext));
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("robomod")
                        .requires(source -> source.hasPermission(4))

                        .then(Commands.literal("setstore")
                                .then(Commands.argument("energy", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            if (ctx.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player) {
                                                HitResult hit = player.pick(player.getBlockReach(), 0.0F, false);
                                                if (hit.getType() == HitResult.Type.BLOCK) {
                                                    BlockEntity be = ctx.getSource().getLevel()
                                                            .getBlockEntity(((BlockHitResult) hit).getBlockPos());
                                                    if (be instanceof EnergyBlockEntity energy) {
                                                        energy.setEnergyStored(IntegerArgumentType.getInteger(ctx, "energy"));
                                                    }
                                                } else {
                                                    ctx.getSource().sendFailure(Component.literal("No Block in range"));
                                                }
                                                return 1;
                                            }
                                            ctx.getSource().sendFailure(Component.literal("Source not Player"));
                                            return 0;
                                        })))

                        .then(Commands.literal("setstoreCyborg")
                                .then(Commands.argument("energy", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(ctx -> {
                                            if (ctx.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player
                                                    && player instanceof PlayerExtension cyborg && cyborg.isCyborg()) {
                                                cyborg.setEnergyStored(IntegerArgumentType.getInteger(ctx, "energy"));
                                                return 1;
                                            }
                                            ctx.getSource().sendFailure(Component.literal("Source not Player or not a Cyborg"));
                                            return 0;
                                        })))
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("RoboMod client setup running.");
        }
    }
}
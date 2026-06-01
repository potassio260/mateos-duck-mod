package net.mateo.robomod.item;

import net.mateo.robomod.client.util.ClientJetpackHandler;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.util.JetpackUseTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JetpackModule extends CyborgModuleItem {

    public JetpackModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension ex, ItemStack stack) {
        if (ex.isCyborg()
                && ex.containsModule(ModItems.JETPACK_MODULE.get())
                && !ex.containsModule(ModItems.FLIGHT_MODULE.get())
                && !player.isCreative()
                && !player.isSpectator()) {

            if (JetpackUseTracker.usesJetpack(player.getUUID())) {
                int energyConsume = 4;
                // Replaces EquipmentSlot.CHEST → EquipmentSlot.CHEST (same)
                // Replaces isFallFlying() → isFallFlying() (same)
                if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem
                        && player.isFallFlying()) {
                    energyConsume *= 3;
                }
                if (ex.getEnergyStored() > energyConsume) {
                    ex.setEnergyStored(Math.max(ex.getEnergyStored() - energyConsume, 0));
                    useJetpack(player);
                    spawnJetpackEffects(level, player);
                    player.fallDistance = 0;
                }
            }
        }
    }

    public static void useJetpack(Player player) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem
                && player.isFallFlying()) {
            updateElytraVelocity(player);
        } else {
            updateVelocity(player);
        }
    }

    public static void updateVelocity(Player player) {
        double newVerticalVelocity = player.getDeltaMovement().y + 0.1;
        if (newVerticalVelocity < 0.5) {
            // Replaces addVelocity → push
            player.push(0, 0.1, 0);
        }
    }

    private static void updateElytraVelocity(Player player) {
        Vec3 velocity   = player.getDeltaMovement();
        // Replaces getRotationVector() → getLookAngle()
        Vec3 lookVector = player.getLookAngle();
        Vec3 boost = new Vec3(
                lookVector.x * 0.1,
                0.05 + lookVector.y * 0.05,
                lookVector.z * 0.1
        );
        Vec3 newVelocity = velocity.add(boost);
        double speedLimit = 1.5;
        if (newVelocity.length() > speedLimit) {
            newVelocity = newVelocity.normalize().scale(speedLimit);
        }
        player.setDeltaMovement(newVelocity);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(ClientLevel level, Player player, PlayerExtension extension) {
        ClientJetpackHandler.tick();
    }

    private static void spawnJetpackEffects(ServerLevel level, Player player) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem
                && player.isFallFlying()) {
            if (level.getRandom().nextFloat() < 0.1F) {
                level.playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIREWORK_ROCKET_LAUNCH,
                        SoundSource.PLAYERS, 0.2F, 1.5F);
            }
        } else {
            if (level.getRandom().nextFloat() < 0.2F) {
                level.playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FURNACE_FIRE_CRACKLE,
                        SoundSource.PLAYERS,
                        0.4F, 1.0F + (player.getRandom().nextFloat() * 0.3F - 0.15F));
            }
        }

        // Replaces getRotationVector(0, bodyYaw) → no pitch override; use yHeadRot directly
        float yaw = (float) Math.toRadians(player.yBodyRot);
        double lookX = -Math.sin(yaw);
        double lookZ =  Math.cos(yaw);

        spawnParticles(level, player, ParticleTypes.FLAME,
                player.getX() - lookX * 0.35,
                player.getY() + 1,
                player.getZ() - lookZ * 0.35,
                1, 0, 0, 0, 0.03);
    }

    static void spawnParticles(ServerLevel level, Player jetpackUser, ParticleOptions particle,
                               double x, double y, double z,
                               int count, double dx, double dy, double dz, double speed) {
        // Replaces ParticleS2CPacket → ClientboundLevelParticlesPacket
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                particle, false, x, y, z,
                (float) dx, (float) dy, (float) dz, (float) speed, count);

        for (ServerPlayer player : level.players()) {
            if (!player.getUUID().equals(jetpackUser.getUUID())) {
                // Replaces sendToPlayerIfNearby → sendToPlayer directly (Forge has no exact equivalent, distance check optional)
                player.connection.send(packet);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Double jump to start ascending"));
        tooltip.add(Component.literal("and hold to continue flying."));
        tooltip.add(Component.literal("Consumes §b4/t §7of energy."));
    }
}

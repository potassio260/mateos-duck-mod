package net.mateo.robomod.client.util;

import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.item.JetpackModule;
import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.network.packet.UseJetpackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.phys.Vec3;

public class ClientJetpackHandler {

    private static boolean jumped                  = false;
    private static long    lastJumpTime            = 0;
    private static boolean jetpackActive           = false;
    private static boolean hasActivatedThisFlight  = false;
    private static boolean usingJetpack            = false;

    private static final long DOUBLE_JUMP_THRESHOLD = 300;

    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player instanceof PlayerExtension ex
                && ex.isCyborg()
                && ex.containsModule(ModItems.JETPACK_MODULE.get())
                && !ex.containsModule(ModItems.FLIGHT_MODULE.get())
                && !player.isCreative()
                && !player.isSpectator()) {

            boolean isHoldingJump = Minecraft.getInstance().options.keyJump.isDown();

            if (player.onGround()) {
                hasActivatedThisFlight = false;
                jetpackActive          = false;
            }

            if (isHoldingJump && !jumped) {
                handleJumpPress(player);
            }

            if (jetpackActive && isInAir(player) && isHoldingJump) {
                if (ex.getEnergyStored() > 0) {
                    spawnJetpackEffects(player);
                    JetpackModule.useJetpack(player);
                    player.fallDistance = 0;

                    if (!usingJetpack) {
                        // Replace ClientPlayNetworking.send() with your Forge channel send
                        ModPackets.CHANNEL.sendToServer(new UseJetpackPacket(true));
                        usingJetpack = true;
                    }
                } else {
                    deactivateJetpack(player);
                }
            } else {
                if (usingJetpack) {
                    ModPackets.CHANNEL.sendToServer(new UseJetpackPacket(false));
                    usingJetpack = false;
                }
            }

            jumped = isHoldingJump;
        }
    }

    private static boolean isUsingElytra(LocalPlayer player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem
                && player.isFallFlying();
    }

    private static boolean isInAir(LocalPlayer player) {
        return !player.onGround() && !player.isInWater() && !player.onClimbable();
    }

    private static void handleJumpPress(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();

        if (isInAir(player)) {
            if (currentTime - lastJumpTime < DOUBLE_JUMP_THRESHOLD) {
                if (!hasActivatedThisFlight && activateJetpack(player)) {
                    hasActivatedThisFlight = true;
                    jetpackActive          = true;
                }
            } else if (hasActivatedThisFlight) {
                jetpackActive = true;
            }
        }

        lastJumpTime = currentTime;
    }

    private static boolean activateJetpack(LocalPlayer player) {
        if (((PlayerExtension) player).getEnergyStored() <= 0) {
            player.playSound(SoundEvents.FIRE_EXTINGUISH, 0.3F, 1.0F);
            return false;
        }

        if (isUsingElytra(player)) {
            elytraHeadStart(player);
            player.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 0.3F, 1.2F);
        } else {
            Vec3 velocity     = player.getDeltaMovement();
            double boostVelocity = 0.3;

            if (velocity.y < 0.5) {
                double newVerticalVelocity = Math.max(velocity.y + boostVelocity, boostVelocity);
                player.setDeltaMovement(velocity.x, newVerticalVelocity, velocity.z);
            }
            player.playSound(SoundEvents.GHAST_SHOOT, 0.3F, 1.5F);
        }

        spawnActivationParticles(player);
        return true;
    }

    private static void elytraHeadStart(Player player) {
        Vec3 velocity   = player.getDeltaMovement();
        Vec3 lookVector = player.getViewVector(1.0F);

        Vec3 boostVector = new Vec3(
                lookVector.x * 0.5,
                0.2 + (lookVector.y * 0.3),
                lookVector.z * 0.5
        );

        Vec3 newVelocity = velocity.add(boostVector);

        double speedLimit = 1.5;
        if (newVelocity.length() > speedLimit) {
            newVelocity = newVelocity.normalize().scale(speedLimit);
        }

        player.setDeltaMovement(newVelocity);
    }

    private static void deactivateJetpack(LocalPlayer player) {
        jetpackActive = false;
        player.playSound(SoundEvents.FIRE_EXTINGUISH, 0.3F, 1.2F);
    }

    private static void spawnActivationParticles(LocalPlayer player) {
        if (isUsingElytra(player)) {
            Vec3 lookVector = player.getViewVector(1.0F);
            player.level().addParticle(ParticleTypes.FIREWORK,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    lookVector.x * 0.5, lookVector.y * 0.5, lookVector.z * 0.5);
        }
    }

    private static void spawnJetpackEffects(LocalPlayer player) {
        if (isUsingElytra(player)) {
            Vec3 lookVector = player.getViewVector(1.0F);
            player.level().addParticle(ParticleTypes.FLAME,
                    player.getX() - (lookVector.x * 2),
                    player.getY() - (lookVector.y * 0.5),
                    player.getZ() - (lookVector.z * 2),
                    -lookVector.x * 0.3, -lookVector.y * 0.3, -lookVector.z * 0.3);
        } else {
            // getRotationVector(pitch, yaw) → use getViewVector with yBodyRot
            Vec3 lookVector = player.getViewVector(0);
            // Replicate body-yaw-only look: forward direction ignoring pitch
            double rad = Math.toRadians(player.yBodyRot);
            double lx  = -Math.sin(rad);
            double lz  =  Math.cos(rad);

            player.level().addParticle(ParticleTypes.FLAME,
                    player.getX() - (lx * 0.35),
                    player.getY() + 1,
                    player.getZ() - (lz * 0.35),
                    0.0, -0.1, 0.0);
        }
    }
}

package net.mateo.robomod.network;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.network.packet.SyncCyborgStatePacket;
import net.mateo.robomod.packet.DebugCablePacket;
import net.mateo.robomod.network.packet.EnergyGuiUpdatePacket;
import net.mateo.robomod.network.packet.UseJetpackPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Central packet channel for RoboMod.
 *
 * Pattern: one SimpleChannel, each packet gets an incrementing discriminator id.
 * Add new packets by incrementing the id counter and calling CHANNEL.registerMessage.
 */
public class ModPackets {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RoboMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        // S→C: sent to a specific client player
        CHANNEL.registerMessage(id++,
                EnergyGuiUpdatePacket.class,
                EnergyGuiUpdatePacket::encode,
                EnergyGuiUpdatePacket::decode,
                EnergyGuiUpdatePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        CHANNEL.registerMessage(id++,
                DebugCablePacket.class,
                DebugCablePacket::encode,
                DebugCablePacket::decode,
                DebugCablePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        // C→S: sent from client to server
        CHANNEL.registerMessage(id++,
                UseJetpackPacket.class,
                UseJetpackPacket::encode,
                UseJetpackPacket::decode,
                UseJetpackPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_SERVER));

        CHANNEL.registerMessage(id++,
                SyncCyborgStatePacket.class,
                SyncCyborgStatePacket::encode,
                SyncCyborgStatePacket::decode,
                SyncCyborgStatePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /**
     * Send a packet to a specific server player (server → client).
     * Called from menus and block entities.
     */
    public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * Called from ClientSetup to register any client-side receivers that aren't
     * handled automatically by the packet's handle() consumer.
     * For SimpleChannel with per-packet handle() lambdas this is typically a no-op.
     */
    public static void registerClientReceivers() {
        // No-op: each packet's handle() consumer already runs on the correct thread.
        // Add any extra client-only setup here if needed.
    }
}
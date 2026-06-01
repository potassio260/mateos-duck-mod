package net.mateo.robomod.network.packet;

import net.mateo.robomod.util.JetpackUseTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseJetpackPacket {

    private final boolean useJetpack;

    public UseJetpackPacket(boolean useJetpack) {
        this.useJetpack = useJetpack;
    }

    // -------------------------------------------------------------------------
    // Encode / Decode
    // -------------------------------------------------------------------------

    public static void encode(UseJetpackPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.useJetpack);
    }

    public static UseJetpackPacket decode(FriendlyByteBuf buf) {
        return new UseJetpackPacket(buf.readBoolean());
    }

    // -------------------------------------------------------------------------
    // Handler — runs on the SERVER (C2S packet)
    // Replaces ServerPlayNetworking.registerGlobalReceiver + DISCONNECT cleanup.
    //
    // Player disconnect cleanup should be done in a separate @SubscribeEvent
    // for PlayerEvent.PlayerLoggedOutEvent on the Forge event bus, e.g.:
    //
    //   @SubscribeEvent
    //   public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
    //       JetpackUseTracker.removePlayer(event.getEntity().getUUID());
    //   }
    // -------------------------------------------------------------------------

    public static void handle(UseJetpackPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender(); // non-null for C2S packets
            if (player != null) {
                JetpackUseTracker.setUseJetpack(player.getUUID(), packet.useJetpack);
            }
        });
        ctx.setPacketHandled(true);
    }

    // Accessor
    public boolean useJetpack() { return useJetpack; }
}

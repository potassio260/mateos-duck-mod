package net.mateo.robomod.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DebugCablePacket {

    // --- Client-side state (only accessed on the client) ---
    public static BlockPos ownerCable = null;
    public static List<BlockPos> debugCables = new ArrayList<>();

    private final BlockPos blockPos;
    private final boolean clean;
    private final boolean isOwner;

    public DebugCablePacket(BlockPos blockPos, boolean clean, boolean isOwner) {
        this.blockPos = blockPos;
        this.clean    = clean;
        this.isOwner  = isOwner;
    }

    // -------------------------------------------------------------------------
    // Encode / Decode  (replaces PacketCodec)
    // -------------------------------------------------------------------------

    public static void encode(DebugCablePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.blockPos);
        buf.writeBoolean(packet.clean);
        buf.writeBoolean(packet.isOwner);
    }

    public static DebugCablePacket decode(FriendlyByteBuf buf) {
        BlockPos pos  = buf.readBlockPos();
        boolean clean = buf.readBoolean();
        boolean owner = buf.readBoolean();
        return new DebugCablePacket(pos, clean, owner);
    }

    // -------------------------------------------------------------------------
    // Handler — runs on the CLIENT (S2C packet)
    // Enqueue work so the logic runs on the main game thread.
    // -------------------------------------------------------------------------

    public static void handle(DebugCablePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> getCables(packet.blockPos, packet.clean, packet.isOwner));
        ctx.setPacketHandled(true);
    }

    // -------------------------------------------------------------------------
    // Logic  (unchanged from Fabric)
    // -------------------------------------------------------------------------

    public static void getCables(BlockPos blockPos, boolean clean, boolean isOwner) {
        if (!debugCables.contains(blockPos)) {
            debugCables.add(blockPos);
        }
        if (clean) {
            debugCables.clear();
            return;
        }
        if (isOwner) {
            ownerCable = blockPos;
        }
    }

    // Accessors
    public BlockPos blockPos() { return blockPos; }
    public boolean  clean()    { return clean; }
    public boolean  isOwner()  { return isOwner; }
}

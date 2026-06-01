package net.mateo.robomod.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergyGuiUpdatePacket {

    private final int storedEnergy;
    private final int capacity;

    public EnergyGuiUpdatePacket(int storedEnergy, int capacity) {
        this.storedEnergy = storedEnergy;
        this.capacity     = capacity;
    }

    // -------------------------------------------------------------------------
    // Encode / Decode
    // -------------------------------------------------------------------------

    public static void encode(EnergyGuiUpdatePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.storedEnergy);
        buf.writeInt(packet.capacity);
    }

    public static EnergyGuiUpdatePacket decode(FriendlyByteBuf buf) {
        return new EnergyGuiUpdatePacket(buf.readInt(), buf.readInt());
    }

    // -------------------------------------------------------------------------
    // Handler — runs on the CLIENT (S2C packet)
    // Update your GUI/screen here. Import your AbstractContainerScreen subclass
    // and call its update method, or use Minecraft.getInstance().screen.
    // -------------------------------------------------------------------------

    public static void handle(EnergyGuiUpdatePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // TODO: update the open GUI with packet.storedEnergy / packet.capacity
            // Example:
            // Minecraft mc = Minecraft.getInstance();
            // if (mc.screen instanceof YourEnergyScreen screen) {
            //     screen.updateEnergy(packet.storedEnergy, packet.capacity);
            // }
        });
        ctx.setPacketHandled(true);
    }

    // Accessors
    public int storedEnergy() { return storedEnergy; }
    public int capacity()     { return capacity; }
}

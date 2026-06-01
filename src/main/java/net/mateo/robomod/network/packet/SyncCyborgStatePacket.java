package net.mateo.robomod.network.packet;

import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCyborgStatePacket {
    private final boolean isCyborg;
    private final int energy;
    private final ItemStack head, body, rArm, lArm, rLeg, lLeg;
    private final ItemStack mod1, mod2, mod3, mod4;

    public SyncCyborgStatePacket(PlayerExtension ext) {
        this.isCyborg = ext.isCyborg();
        this.energy = ext.getEnergyStored();
        this.head = ext.getCyborgHead();
        this.body = ext.getCyborgBody();
        this.rArm = ext.getCyborgRightArm();
        this.lArm = ext.getCyborgLeftArm();
        this.rLeg = ext.getCyborgRightLeg();
        this.lLeg = ext.getCyborgLeftLeg();
        this.mod1 = ext.getModule1();
        this.mod2 = ext.getModule2();
        this.mod3 = ext.getModule3();
        this.mod4 = ext.getModule4();
    }

    public SyncCyborgStatePacket(FriendlyByteBuf buf) {
        this.isCyborg = buf.readBoolean();
        this.energy = buf.readInt();
        this.head = buf.readItem();
        this.body = buf.readItem();
        this.rArm = buf.readItem();
        this.lArm = buf.readItem();
        this.rLeg = buf.readItem();
        this.lLeg = buf.readItem();
        this.mod1 = buf.readItem();
        this.mod2 = buf.readItem();
        this.mod3 = buf.readItem();
        this.mod4 = buf.readItem();
    }

    // ADDED: The decode method your ModPackets.java is looking for
    public static SyncCyborgStatePacket decode(FriendlyByteBuf buf) {
        return new SyncCyborgStatePacket(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isCyborg);
        buf.writeInt(energy);
        buf.writeItem(head);
        buf.writeItem(body);
        buf.writeItem(rArm);
        buf.writeItem(lArm);
        buf.writeItem(rLeg);
        buf.writeItem(lLeg);
        buf.writeItem(mod1);
        buf.writeItem(mod2);
        buf.writeItem(mod3);
        buf.writeItem(mod4);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player instanceof PlayerExtension ext) {
                ext.setCyborg(isCyborg);
                ext.setEnergyStored(energy);
                ext.setCyborgHead(head);
                ext.setCyborgBody(body);
                ext.setCyborgRightArm(rArm);
                ext.setCyborgLeftArm(lArm);
                ext.setCyborgRightLeg(rLeg);
                ext.setCyborgLeftLeg(lLeg);
                ext.setModule1(mod1);
                ext.setModule2(mod2);
                ext.setModule3(mod3);
                ext.setModule4(mod4);

                System.out.println("[SyncCyborgPacket] Applied. isCyborg=" + isCyborg);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}
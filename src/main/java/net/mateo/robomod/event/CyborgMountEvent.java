package net.mateo.robomod.event;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.entity.CyborgEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CyborgMountEvent {

    @SubscribeEvent
    public static void onEntityDismount(EntityMountEvent event) {
        if (!event.isDismounting()) return;

        if (!(event.getEntityBeingMounted() instanceof CyborgEntity cyborg)) return;

        // Allow the server code in becomeFlesh to successfully unmount
        // the player if the cyborg entity is being safely discarded.
        if (cyborg.isRemoved()) return;

        // Block ALL player-initiated dismounts (Client & Server).
        // By checking 'Player' generally, it catches both LocalPlayer and ServerPlayer.
        if (event.getEntityMounting() instanceof Player) {
            event.setCanceled(true);
        }
    }
}
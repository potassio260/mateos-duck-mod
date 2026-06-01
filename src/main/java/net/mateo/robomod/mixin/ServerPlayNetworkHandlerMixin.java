package net.mateo.robomod.mixin;

import net.mateo.robomod.item.ModItems;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.mateo.robomod.extension.PlayerExtension;
// Make sure to import your ModItems here!

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayer player;

    // These are the 1.20.1 Mojang mapped fields that track how long a player has been floating
    @Shadow private int aboveGroundTickCount;
    @Shadow private int aboveGroundVehicleTickCount;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void resetFloatingTicksForJetpack(CallbackInfo ci) {
        if (this.player instanceof PlayerExtension ex
                && ex.isCyborg()
                && ex.containsModule(ModItems.JETPACK_MODULE.get())) { // Update this to match your item registry

            // By constantly resetting these to 0, the server thinks the player
            // is safely on the ground and will never kick them for flying.
            this.aboveGroundTickCount = 0;
            this.aboveGroundVehicleTickCount = 0;
        }
    }
}
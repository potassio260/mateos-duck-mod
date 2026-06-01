package net.mateo.robomod.mixin.toughasnails;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.thirst.ThirstData;
import net.mateo.robomod.compat.ThirstDataExtension;

// ThirstData is a plain Java class in TAN for both Fabric and Forge,
// so this Mixin translates 1-to-1. Only the package paths change.
@Mixin(ThirstData.class)
public class ThirstDataMixin implements ThirstDataExtension {

    boolean isCyborg = false;

    @Inject(method = "getThirst", at = @At("HEAD"), remap = false, cancellable = true)
    void getThirst(CallbackInfoReturnable<Integer> cir) {
        if (isCyborg) cir.setReturnValue(20);
    }

    @Inject(method = "setThirst", at = @At("HEAD"), remap = false, cancellable = true)
    void setThirst(int level, CallbackInfo ci) {
        if (isCyborg) ci.cancel();
    }

    @Override
    public void setCyborg(boolean isCyborg) {
        this.isCyborg = isCyborg;
    }
}

package net.mateo.robomod.mixin;

import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.BaseCyborgModuleItem;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.PlayerEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin implements PlayerExtension {

    @Unique private boolean robomod$isCyborg = false;

    @Unique private ItemStack robomod$head     = ItemStack.EMPTY;
    @Unique private ItemStack robomod$body     = ItemStack.EMPTY;
    @Unique private ItemStack robomod$rightArm = ItemStack.EMPTY;
    @Unique private ItemStack robomod$leftArm  = ItemStack.EMPTY;
    @Unique private ItemStack robomod$rightLeg = ItemStack.EMPTY;
    @Unique private ItemStack robomod$leftLeg  = ItemStack.EMPTY;

    @Unique private ItemStack robomod$module1 = ItemStack.EMPTY;
    @Unique private ItemStack robomod$module2 = ItemStack.EMPTY;
    @Unique private ItemStack robomod$module3 = ItemStack.EMPTY;
    @Unique private ItemStack robomod$module4 = ItemStack.EMPTY;

    @Unique private PlayerEnergyStorage robomod$energyStorage = new PlayerEnergyStorage(10000);

    @Override public boolean isCyborg()                  { return robomod$isCyborg; }
    @Override public void setCyborg(boolean isCyborg)    { robomod$isCyborg = isCyborg; }

    @Override public ItemStack getCyborgHead()                      { return robomod$head; }
    @Override public void setCyborgHead(ItemStack head)             { robomod$head = head; }

    @Override public ItemStack getCyborgBody()                      { return robomod$body; }
    @Override public void setCyborgBody(ItemStack body)             { robomod$body = body; }

    @Override public ItemStack getCyborgRightArm()                  { return robomod$rightArm; }
    @Override public void setCyborgRightArm(ItemStack rightArm)     { robomod$rightArm = rightArm; }

    @Override public ItemStack getCyborgLeftArm()                   { return robomod$leftArm; }
    @Override public void setCyborgLeftArm(ItemStack leftArm)       { robomod$leftArm = leftArm; }

    @Override public ItemStack getCyborgRightLeg()                  { return robomod$rightLeg; }
    @Override public void setCyborgRightLeg(ItemStack rightLeg)     { robomod$rightLeg = rightLeg; }

    @Override public ItemStack getCyborgLeftLeg()                   { return robomod$leftLeg; }
    @Override public void setCyborgLeftLeg(ItemStack leftLeg)       { robomod$leftLeg = leftLeg; }

    @Override public ItemStack getModule1()                  { return robomod$module1; }
    @Override public void setModule1(ItemStack module)       { robomod$module1 = module; }

    @Override public ItemStack getModule2()                  { return robomod$module2; }
    @Override public void setModule2(ItemStack module)       { robomod$module2 = module; }

    @Override public ItemStack getModule3()                  { return robomod$module3; }
    @Override public void setModule3(ItemStack module)       { robomod$module3 = module; }

    @Override public ItemStack getModule4()                  { return robomod$module4; }
    @Override public void setModule4(ItemStack module)       { robomod$module4 = module; }

    @Override
    public List<ItemStack> getModules() {
        return List.of(robomod$module1, robomod$module2, robomod$module3, robomod$module4);
    }

    @Override
    public boolean containsModule(Item module) {
        return robomod$module1.is(module) || robomod$module2.is(module)
                || robomod$module3.is(module) || robomod$module4.is(module);
    }

    @Override public EnergyStorage getEnergyStorage()      { return robomod$energyStorage; }
    @Override public int getEnergyStored()                 { return robomod$energyStorage.getEnergy(); }
    @Override public int getCapacity()                     { return robomod$energyStorage.capacity(); }
    @Override public void setEnergyStored(int energy)      { robomod$energyStorage.setEnergy(energy); }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void robomod$tick(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        Minecraft mc = Minecraft.getInstance();

        // Fix camera — move it to the CyborgEntity when possessing
        if (isCyborg() && self.getVehicle() instanceof CyborgEntity cyborg) {
            mc.setCameraEntity(cyborg);
        } else if (!isCyborg() && mc.getCameraEntity() instanceof CyborgEntity) {
            mc.setCameraEntity(self);
        }

        if (!isCyborg()) return;

        // Module client ticks
        getModules().forEach(stack -> {
            if (stack.getItem() instanceof BaseCyborgModuleItem moduleItem) {
                moduleItem.clientTick((ClientLevel) self.level(), self, this);
            }
        });
    }
}
package net.mateo.robomod.mixin;

import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.util.transfer.PlayerEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements PlayerExtension {

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

    // ── Getters / Setters ────────────────────────────────────────────────────

    @Override public boolean isCyborg()               { return robomod$isCyborg; }
    @Override public void setCyborg(boolean isCyborg) { robomod$isCyborg = isCyborg; }

    @Override public ItemStack getCyborgHead()                  { return robomod$head; }
    @Override public void setCyborgHead(ItemStack head)         { robomod$head = head; }

    @Override public ItemStack getCyborgBody()                  { return robomod$body; }
    @Override public void setCyborgBody(ItemStack body)         { robomod$body = body; }

    @Override public ItemStack getCyborgRightArm()              { return robomod$rightArm; }
    @Override public void setCyborgRightArm(ItemStack rightArm) { robomod$rightArm = rightArm; }

    @Override public ItemStack getCyborgLeftArm()               { return robomod$leftArm; }
    @Override public void setCyborgLeftArm(ItemStack leftArm)   { robomod$leftArm = leftArm; }

    @Override public ItemStack getCyborgRightLeg()              { return robomod$rightLeg; }
    @Override public void setCyborgRightLeg(ItemStack rightLeg) { robomod$rightLeg = rightLeg; }

    @Override public ItemStack getCyborgLeftLeg()               { return robomod$leftLeg; }
    @Override public void setCyborgLeftLeg(ItemStack leftLeg)   { robomod$leftLeg = leftLeg; }

    @Override public ItemStack getModule1()             { return robomod$module1; }
    @Override public void setModule1(ItemStack module)  { robomod$module1 = module; }

    @Override public ItemStack getModule2()             { return robomod$module2; }
    @Override public void setModule2(ItemStack module)  { robomod$module2 = module; }

    @Override public ItemStack getModule3()             { return robomod$module3; }
    @Override public void setModule3(ItemStack module)  { robomod$module3 = module; }

    @Override public ItemStack getModule4()             { return robomod$module4; }
    @Override public void setModule4(ItemStack module)  { robomod$module4 = module; }

    @Override
    public List<ItemStack> getModules() {
        return List.of(robomod$module1, robomod$module2, robomod$module3, robomod$module4);
    }

    @Override
    public boolean containsModule(Item module) {
        return robomod$module1.is(module) || robomod$module2.is(module)
                || robomod$module3.is(module) || robomod$module4.is(module);
    }

    @Override public PlayerEnergyStorage getEnergyStorage()   { return robomod$energyStorage; }
    @Override public int getEnergyStored()                    { return robomod$energyStorage.getEnergy(); }
    @Override public int getCapacity()                        { return robomod$energyStorage.capacity(); }
    @Override public void setEnergyStored(int energy)         { robomod$energyStorage.setEnergy(energy); }

    // ── NBT persistence ──────────────────────────────────────────────────────
    // Without these, all cyborg state is lost on death or world reload.

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void robomod$save(CompoundTag tag, CallbackInfo ci) {
        CompoundTag roboTag = new CompoundTag();
        roboTag.putBoolean("isCyborg", robomod$isCyborg);
        roboTag.putInt("energy", robomod$energyStorage.getEnergy());
        roboTag.put("head",     robomod$head.save(new CompoundTag()));
        roboTag.put("body",     robomod$body.save(new CompoundTag()));
        roboTag.put("rightArm", robomod$rightArm.save(new CompoundTag()));
        roboTag.put("leftArm",  robomod$leftArm.save(new CompoundTag()));
        roboTag.put("rightLeg", robomod$rightLeg.save(new CompoundTag()));
        roboTag.put("leftLeg",  robomod$leftLeg.save(new CompoundTag()));
        roboTag.put("module1",  robomod$module1.save(new CompoundTag()));
        roboTag.put("module2",  robomod$module2.save(new CompoundTag()));
        roboTag.put("module3",  robomod$module3.save(new CompoundTag()));
        roboTag.put("module4",  robomod$module4.save(new CompoundTag()));
        tag.put("robomod", roboTag);
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void robomod$load(CompoundTag tag, CallbackInfo ci) {
        if (!tag.contains("robomod")) return;
        CompoundTag roboTag = tag.getCompound("robomod");
        robomod$isCyborg = roboTag.getBoolean("isCyborg");
        robomod$energyStorage.setEnergy(roboTag.getInt("energy"));
        robomod$head     = ItemStack.of(roboTag.getCompound("head"));
        robomod$body     = ItemStack.of(roboTag.getCompound("body"));
        robomod$rightArm = ItemStack.of(roboTag.getCompound("rightArm"));
        robomod$leftArm  = ItemStack.of(roboTag.getCompound("leftArm"));
        robomod$rightLeg = ItemStack.of(roboTag.getCompound("rightLeg"));
        robomod$leftLeg  = ItemStack.of(roboTag.getCompound("leftLeg"));
        robomod$module1  = ItemStack.of(roboTag.getCompound("module1"));
        robomod$module2  = ItemStack.of(roboTag.getCompound("module2"));
        robomod$module3  = ItemStack.of(roboTag.getCompound("module3"));
        robomod$module4  = ItemStack.of(roboTag.getCompound("module4"));
    }
}
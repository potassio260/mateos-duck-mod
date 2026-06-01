package net.mateo.robomod.extension;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.item.CyborgPartItem;
import net.mateo.robomod.util.CyborgPartType;
import net.mateo.robomod.util.transfer.EnergyStorage;

import java.util.List;

public interface PlayerExtension {
    boolean isCyborg();
    void setCyborg(boolean isCyborg);

    void setCyborgHead(ItemStack head);
    void setCyborgBody(ItemStack body);
    void setCyborgRightArm(ItemStack rightArm);
    void setCyborgLeftArm(ItemStack leftArm);
    void setCyborgRightLeg(ItemStack rightLeg);
    void setCyborgLeftLeg(ItemStack leftLeg);

    ItemStack getCyborgHead();
    ItemStack getCyborgBody();
    ItemStack getCyborgRightArm();
    ItemStack getCyborgLeftArm();
    ItemStack getCyborgRightLeg();
    ItemStack getCyborgLeftLeg();

    default ItemStack getCyborgPart(CyborgPartType partType) {
        return switch (partType) {
            case HEAD      -> getCyborgHead();
            case BODY      -> getCyborgBody();
            case RIGHT_ARM -> getCyborgRightArm();
            case LEFT_ARM  -> getCyborgLeftArm();
            case RIGHT_LEG -> getCyborgRightLeg();
            case LEFT_LEG  -> getCyborgLeftLeg();
        };
    }

    default void setCyborgPart(CyborgPartType partType, ItemStack stack) {
        switch (partType) {
            case HEAD      -> setCyborgHead(stack);
            case BODY      -> setCyborgBody(stack);
            case RIGHT_ARM -> setCyborgRightArm(stack);
            case LEFT_ARM  -> setCyborgLeftArm(stack);
            case RIGHT_LEG -> setCyborgRightLeg(stack);
            case LEFT_LEG  -> setCyborgLeftLeg(stack);
        }
    }

    default void clearAllParts() {
        setCyborgHead(ItemStack.EMPTY);
        setCyborgBody(ItemStack.EMPTY);
        setCyborgRightArm(ItemStack.EMPTY);
        setCyborgLeftArm(ItemStack.EMPTY);
        setCyborgRightLeg(ItemStack.EMPTY);
        setCyborgLeftLeg(ItemStack.EMPTY);
    }

    default ItemStack getModule(Item module) {
        for (ItemStack stack : getModules()) {
            if (stack.is(module)) return stack;  // isOf() → is()
        }
        return null;
    }

    ItemStack getModule1();
    ItemStack getModule2();
    ItemStack getModule3();
    ItemStack getModule4();

    void setModule1(ItemStack module);
    void setModule2(ItemStack module);
    void setModule3(ItemStack module);
    void setModule4(ItemStack module);

    List<ItemStack> getModules();

    boolean containsModule(Item module);

    void setEnergyStored(int energy);
    int getEnergyStored();
    int getCapacity();
    EnergyStorage getEnergyStorage();

    default void copyFrom(PlayerExtension old) {
        this.setCyborg(old.isCyborg());
        this.setEnergyStored(old.getEnergyStored());

        CyborgPartType.forEach(partType -> this.setCyborgPart(partType, old.getCyborgPart(partType)));

        this.setModule1(old.getModule1());
        this.setModule2(old.getModule2());
        this.setModule3(old.getModule3());
        this.setModule4(old.getModule4());
    }

    default void setupAttributes(Player player) {
        double maxHealth = 0;

        for (CyborgPartType partType : CyborgPartType.values()) {
            if (this.getCyborgPart(partType).getItem() instanceof CyborgPartItem partItem) {
                maxHealth += partItem.getHealth();
            }
        }

        if (maxHealth > 0) {
            var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                // Remove existing modifier first to avoid "already applied" crash
                healthAttr.removeModifier(RoboMod.CYBORG_HEALTH_UUID);

                maxHealth -= player.getAttributeValue(Attributes.MAX_HEALTH);

                healthAttr.addPermanentModifier(
                        new AttributeModifier(
                                RoboMod.CYBORG_HEALTH_UUID,
                                "cyborg_health",
                                maxHealth,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }
        }
    }
}
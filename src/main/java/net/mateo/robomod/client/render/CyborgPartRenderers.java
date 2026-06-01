package net.mateo.robomod.client.render;

import net.mateo.robomod.client.ClientSetup;
import net.mateo.robomod.client.model.AdvancedCyborgModel;
import net.mateo.robomod.client.model.BasicCyborgModel;
import net.mateo.robomod.client.render.parts.*;
import net.mateo.robomod.item.CyborgPartItem;
import net.mateo.robomod.util.CyborgPartType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class CyborgPartRenderers {

    public static final ArrayList<CyborgPartRenderer> PARTS = new ArrayList<>();

    public static final CyborgPartRenderer BASIC_HEAD      = register(new HeadPartRenderer("basic_head",      "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer BASIC_RIGHT_ARM = register(new RightArmPartRenderer("basic_right_arm", "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer BASIC_LEFT_ARM  = register(new LeftArmPartRenderer("basic_left_arm",  "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer BASIC_BODY      = register(new BodyPartRenderer("basic_body",      "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer BASIC_RIGHT_LEG = register(new RightLegPartRenderer("basic_right_leg", "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer BASIC_LEFT_LEG  = register(new LeftLegPartRenderer("basic_left_leg",  "textures/entity/basic_cyborg.png", CyborgPartRenderers::getBasicModel));

    public static final CyborgPartRenderer GOLDEN_HEAD      = register(new HeadPartRenderer("golden_head",      "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer GOLDEN_RIGHT_ARM = register(new RightArmPartRenderer("golden_right_arm", "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer GOLDEN_LEFT_ARM  = register(new LeftArmPartRenderer("golden_left_arm",  "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer GOLDEN_BODY      = register(new BodyPartRenderer("golden_body",      "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer GOLDEN_RIGHT_LEG = register(new RightLegPartRenderer("golden_right_leg", "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));
    public static final CyborgPartRenderer GOLDEN_LEFT_LEG  = register(new LeftLegPartRenderer("golden_left_leg",  "textures/entity/golden_cyborg.png", CyborgPartRenderers::getBasicModel));

    public static final CyborgPartRenderer ADVANCED_HEAD      = register(new HeadPartRenderer("advanced_head",      "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));
    public static final CyborgPartRenderer ADVANCED_RIGHT_ARM = register(new RightArmPartRenderer("advanced_right_arm", "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));
    public static final CyborgPartRenderer ADVANCED_LEFT_ARM  = register(new LeftArmPartRenderer("advanced_left_arm",  "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));
    public static final CyborgPartRenderer ADVANCED_BODY      = register(new BodyPartRenderer("advanced_body",      "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));
    public static final CyborgPartRenderer ADVANCED_RIGHT_LEG = register(new RightLegPartRenderer("advanced_right_leg", "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));
    public static final CyborgPartRenderer ADVANCED_LEFT_LEG  = register(new LeftLegPartRenderer("advanced_left_leg",  "textures/entity/advanced_cyborg.png", CyborgPartRenderers::getAdvancedModel));

    public static CyborgPartRenderer register(CyborgPartRenderer part) {
        PARTS.add(part);
        return part;
    }

    public static AdvancedCyborgModel advanced;
    public static BasicCyborgModel    basic;

    // -------------------------------------------------------------------------
    // LAZY LOADERS: These guarantee the models are never null when requested.
    // -------------------------------------------------------------------------
    public static BasicCyborgModel getBasicModel() {
        if (basic == null) {
            basic = new BasicCyborgModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientSetup.BASIC_CYBORG_LAYER));
        }
        return basic;
    }

    public static AdvancedCyborgModel getAdvancedModel() {
        if (advanced == null) {
            advanced = new AdvancedCyborgModel(Minecraft.getInstance().getEntityModels().bakeLayer(ClientSetup.ADVANCED_CYBORG_LAYER));
        }
        return advanced;
    }

    public static CyborgPartRenderer get(ItemStack item, CyborgPartType partType) {
        if (item.getItem() instanceof CyborgPartItem partItem && partItem.getPartName(partType) != null) {
            for (CyborgPartRenderer renderer : PARTS) {
                if (renderer.name.equals(partItem.getPartName(partType))) return renderer;
            }
        }
        return null;
    }
}
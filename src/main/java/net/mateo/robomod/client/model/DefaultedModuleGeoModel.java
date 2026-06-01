package net.mateo.robomod.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class DefaultedModuleGeoModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {

    public DefaultedModuleGeoModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    protected String subtype() {
        return "module";
    }
}
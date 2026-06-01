package net.mateo.robomod.item;

import net.mateo.robomod.client.render.module.ModuleRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CyborgModuleItem extends BaseCyborgModuleItem {

    @OnlyIn(Dist.CLIENT)
    private ModuleRenderer renderer;

    public CyborgModuleItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void setModuleRenderer(ModuleRenderer renderer) {
        this.renderer = renderer;
    }

    @OnlyIn(Dist.CLIENT)
    public ModuleRenderer getModuleRenderer() {
        return this.renderer;
    }
}

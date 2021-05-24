package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import thallium.fabric.interfaces.IBakedQuad;

@Environment(EnvType.CLIENT)
@Mixin(BakedQuad.class)
public class MixinBakedQuad implements IBakedQuad {

    @Shadow
    public Sprite sprite;

    @Override
    public Sprite getSprite_thallium() {return sprite;}

}
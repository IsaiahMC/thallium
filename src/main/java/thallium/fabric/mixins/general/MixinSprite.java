package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.texture.Sprite;
import thallium.fabric.interfaces.ISprite;

@Mixin(Sprite.class)
public class MixinSprite implements ISprite {

    private boolean needsAnimationUpdate = false;

    @Override
    public void markNeedsAnimationUpdate() {
        needsAnimationUpdate = true;
    }

    @Override
    public void unmarkNeedsAnimationUpdate() {
        needsAnimationUpdate = false;
    }

    @Override
    public boolean needsAnimationUpdate() {
        return needsAnimationUpdate;
    }

}
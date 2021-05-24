package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.texture.Sprite;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.ISprite;

@Mixin(Sprite.class)
public class MixinSprite implements ISprite {

    private boolean needsAnimationUpdate = false;
    private boolean duringNeeds = false;
    private String path;

    @Override
    public void markNeedsAnimationUpdate() {
        if (needsAnimationUpdate) return;
        path = ((Sprite)(Object)this).getId().getPath();

        if (path.contains("water")) { needsAnimationUpdate = ThalliumOptions.animateWater; return; }
        if (path.contains("lava"))  { needsAnimationUpdate = ThalliumOptions.animateLava;  return; }
        if (path.contains("fire"))  { needsAnimationUpdate = ThalliumOptions.animateFire;  return; }
        if (path.contains("portal")){ needsAnimationUpdate = ThalliumOptions.animatePortal; return;}
        if (path.contains("prismarine")) {needsAnimationUpdate = ThalliumOptions.animatePrismarine;return;}
        if (path.contains("block")) duringNeeds = true;

        needsAnimationUpdate = true;
    }

    @Override
    public void unmarkNeedsAnimationUpdate() {
        needsAnimationUpdate = false;
    }

    @Override
    public boolean needsAnimationUpdate() {
        if (path == null) path = ((Sprite)(Object)this).getId().getPath();
        if (duringNeeds && path.contains("command_block")) return ThalliumOptions.animateCmdBlock;
        if (duringNeeds && path.contains("block/")) return ThalliumOptions.animateTextures;

        return needsAnimationUpdate;
    }

}
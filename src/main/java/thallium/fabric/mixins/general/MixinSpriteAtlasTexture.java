package thallium.fabric.mixins.general;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.ISprite;

@Mixin(SpriteAtlasTexture.class)
public abstract class MixinSpriteAtlasTexture extends AbstractTexture {

    @Shadow
    private List<Sprite> animatedSprites;

    private MinecraftClient mc;

    /**
     * @author
     * @reason Optimized
     */
    @Inject(at = @At("HEAD"), method = "tickAnimatedSprites", cancellable = true)
    public void optimizedtickAnimatedSprites(CallbackInfo ci) {
        if (ThalliumOptions.optimizeAnimations) {
            if (this.mc == null)
                mc = MinecraftClient.getInstance();
            mc.getProfiler().push("determineVisibleTextures");
            GlStateManager.bindTexture(getGlId());
            for (Sprite texture : animatedSprites) {
                if (((ISprite) texture).needsAnimationUpdate()) {
                    texture.tickAnimation();
                    ((ISprite) texture).unmarkNeedsAnimationUpdate();
                }
            }
            mc.getProfiler().pop();
            ci.cancel();
        }
    }

}
package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import thallium.fabric.interfaces.ISprite;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public class MixinFireAnimation {


    @Inject(method = "renderFire", at = @At(value = "HEAD"))
    private void thallium_renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, CallbackInfo ci) {
        Sprite sprite = ModelLoader.FIRE_0.getSprite();
        Sprite sprite2 = ModelLoader.FIRE_1.getSprite();
        ((ISprite) sprite).markNeedsAnimationUpdate();
        ((ISprite) sprite2).markNeedsAnimationUpdate();
    }

}
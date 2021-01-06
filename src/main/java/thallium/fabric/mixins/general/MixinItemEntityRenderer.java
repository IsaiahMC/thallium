package thallium.fabric.mixins.general;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(ItemEntityRenderer.class)
public abstract class MixinItemEntityRenderer {

    @Shadow public int getRenderedAmount(ItemStack itemStack){return 0;}
    @Shadow public ItemRenderer itemRenderer;
    @Shadow public Random random;

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void fastRender(ItemEntity itemEntity, float f, float g, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (!ThalliumOptions.fastItemRender)
            return;

        matrix.push();
        ItemStack itemStack = itemEntity.getStack();
        BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);

        matrix.translate(0.0, 0.24f, 0.0);
        matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(itemEntity.method_27314(g)));

        for (int amount = 0; amount < this.getRenderedAmount(itemStack); ++amount)
            this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrix, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, bakedModel);

        matrix.pop();
        ci.cancel();
    }

}
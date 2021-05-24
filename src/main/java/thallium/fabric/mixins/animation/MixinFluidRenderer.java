package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import thallium.fabric.interfaces.CurrentChunkData;
import thallium.fabric.interfaces.IChunkData;
import thallium.fabric.interfaces.ISprite;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {

    @Shadow
    @Final
    private Sprite[] lavaSprites;

    @Shadow
    @Final
    private Sprite[] waterSprites;

    @Inject(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
    private void afterTextureDetermined(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> cir) {
        BuiltChunk chunkData = CurrentChunkData.CURRENT_CHUNK_DATA.get();
        if (chunkData != null) {
            ((IChunkData) chunkData).getVisibleTextures().add(lavaSprites[0]);
            ((IChunkData) chunkData).getVisibleTextures().add(lavaSprites[1]);
            ((IChunkData) chunkData).getVisibleTextures().add(waterSprites[0]);
            ((IChunkData) chunkData).getVisibleTextures().add(waterSprites[1]);
        } else {
            ((ISprite) lavaSprites[0]).markNeedsAnimationUpdate();
            ((ISprite) lavaSprites[1]).markNeedsAnimationUpdate();
            ((ISprite) waterSprites[1]).markNeedsAnimationUpdate();
            ((ISprite) waterSprites[1]).markNeedsAnimationUpdate();
        }
    }

}
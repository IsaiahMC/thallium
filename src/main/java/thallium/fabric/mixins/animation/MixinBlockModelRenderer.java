package thallium.fabric.mixins.animation;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import thallium.fabric.interfaces.CurrentChunkData;
import thallium.fabric.interfaces.IBakedQuad;
import thallium.fabric.interfaces.IChunkData;
import thallium.fabric.interfaces.ISprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer {

    @Redirect(method = "renderSmooth", at = @At(value= "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModel;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Ljava/util/Random;)Ljava/util/List;"))
    private List<BakedQuad> onRenderQuadsSmooth(BakedModel model, BlockState state, Direction d, Random r, BlockRenderView view, BakedModel m, BlockState state2, BlockPos pos, MatrixStack matrix, VertexConsumer con, boolean z, Random r2, long l, int i) {
        List<BakedQuad> list = model.getQuads(state, d, r);
        markQuads(list);
        return list;
    }

    @Inject(method = "renderQuadsFlat", at = @At("HEAD"))
    private void onRenderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, BitSet flags, CallbackInfo ci) {
        markQuads(quads);
    }

    @Inject(method = "renderQuad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/VertexConsumer;FFFLjava/util/List;II)V", at = @At("HEAD"))
    private static void onRenderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float f, float g, float h, List<BakedQuad> quads, int i, int j, CallbackInfo ci) {
        markQuads(quads);
    }

    @Unique
    private static void markQuads(List<BakedQuad> quads) {
        BuiltChunk chunkData = CurrentChunkData.CURRENT_CHUNK_DATA.get();
        if (chunkData != null) {
            Set<Sprite> visibleTextures = ((IChunkData) chunkData).getVisibleTextures();
            for (BakedQuad quad : quads) {
                Sprite sprite = ((IBakedQuad) quad).getSprite_thallium();
                if (sprite != null) visibleTextures.add(sprite);
            }
        } else {
            for (BakedQuad quad : quads) {
                Sprite sprite = ((IBakedQuad) quad).getSprite_thallium();
                if (sprite != null) ((ISprite) sprite).markNeedsAnimationUpdate();
            }
        }
    }
}
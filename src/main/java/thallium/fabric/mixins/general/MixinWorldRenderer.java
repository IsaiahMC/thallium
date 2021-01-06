package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.ChunkInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import thallium.fabric.interfaces.IWorldRenderer;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IWorldRenderer {

    @Shadow
    @Final
    private ObjectList<ChunkInfo> visibleChunks;

    @Override
    public ObjectList<ChunkInfo> getChunkInfo() {
        return visibleChunks;
    }

    /**
     * @reason Disable sky
     * @author ThalliumMod
     */
    @Overwrite
    public void renderSky(MatrixStack matrices, float tickDelta) {
    }

}
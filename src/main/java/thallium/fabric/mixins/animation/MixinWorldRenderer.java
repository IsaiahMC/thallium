package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.ChunkInfo;
import net.minecraft.client.util.math.MatrixStack;
import thallium.fabric.gui.EnumFogType;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.IWorldRenderer;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IWorldRenderer {

    @Shadow
    private ObjectList<ChunkInfo> visibleChunks;

    @Override
    public ObjectList<ChunkInfo> getChunkInfo() {
        return visibleChunks;
    }

    /**
     * @reason Disable sky
     * @author ThalliumMod
     */
    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
    public void thallium_noSky(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ThalliumOptions.fogType == EnumFogType.OFF) {
            ci.cancel();
        }
    }

}
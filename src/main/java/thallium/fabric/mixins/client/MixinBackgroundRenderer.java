package thallium.fabric.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import thallium.fabric.gui.EnumFogType;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(at = @At("HEAD"), method = "applyFog", cancellable = true)
    private static void disableFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        if (ThalliumOptions.fogType == EnumFogType.OFF)
            ci.cancel();
    }

}
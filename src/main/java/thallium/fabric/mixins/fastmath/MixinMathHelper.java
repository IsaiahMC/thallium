package thallium.fabric.mixins.fastmath;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.MathHelper;
import thallium.fabric.math.MathUtils;

@Mixin(MathHelper.class)
public class MixinMathHelper {

    @Shadow
    @Final
    private static float[] SINE_TABLE;

    @Overwrite
    public static float sin(float value) {
        return MathUtils.fastSin(value);
    }

    @Overwrite
    public static float cos(float value) {
        return MathUtils.fastCos(value);
    }

}
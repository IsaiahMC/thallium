package thallium.fabric.mixins.fastmath;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(Vec3d.class)
public class MixinVec3d {

    @Shadow public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
    @Shadow public double x;
    @Shadow public double y;
    @Shadow public double z;

    /**
     * @reason Avoid creating new Vec3d object
     * @author ThalliumMod
     */
    @Overwrite
    public Vec3d multiply(double multX, double multY, double multZ) {
        // Avoid creating new Vec3d
        this.x *= multX;
        this.y *= multX;
        this.z *= multX;
        return (Vec3d)(Object)this;
    }

    /**
     * @reason Avoid creating new Vec3d object
     * @author ThalliumMod
     */
    @Overwrite
    public Vec3d normalize() {
        double d = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (d < 1.0E-4)
            return ZERO;

        this.x /= d;
        this.y /= d;
        this.z /= d;
        return (Vec3d)(Object)this;
    }

    /**
     * @reason Avoid creating new Vec3d object
     * @author ThalliumMod
     */
    @Overwrite
    public Vec3d rotateX(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        this.y = this.y * (double)f + this.z * (double)g;
        this.z = this.z * (double)f - this.y * (double)g;
        return (Vec3d)(Object)this;
    }

    /**
     * @reason Avoid creating new Vec3d object
     * @author ThalliumMod
     */
    @Overwrite
    public Vec3d rotateY(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        this.x = this.x * (double)f + this.z * (double)g;
        this.z = this.z * (double)f - this.x * (double)g;
        return (Vec3d)(Object)this;
    }


}
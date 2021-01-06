package thallium.fabric.mixins.fastmath;

import java.util.Timer;
import java.util.TimerTask;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(BipedEntityModel.class)
public class MixinBipedEntityModel<T extends LivingEntity> {

    @Shadow public ModelPart head;
    @Shadow public ModelPart torso;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightLeg;
    @Shadow public ModelPart leftLeg;
    @Shadow public float leaningPitch;
    @Shadow public ArmPose leftArmPose;
    @Shadow public ArmPose rightArmPose;
    @Shadow public ModelPart helmet;

    @Shadow protected Arm getPreferredArm(T entity) {return null;}
    @Shadow protected ModelPart getArm(Arm arm) {return null;}
    @Shadow public float lerpAngle(float f, float g, float h) {return 0;}
    @Shadow public void method_30154(T livingEntity) {}
    @Shadow public void method_30155(T livingEntity) {}

    @Overwrite
    public void method_29353(T livingEntity, float f) {
        float handSwingProgress = ((EntityModel<?>)(Object)this).handSwingProgress;
        if (handSwingProgress <= 0.0f)
            return;

        Arm arm = this.getPreferredArm(livingEntity);
        ModelPart modelPart = this.getArm(arm);
        float g = handSwingProgress;

        if (!ThalliumOptions.fastPlayerModel) {
            this.torso.yaw = MathHelper.sin(MathHelper.sqrt(g) * ((float)Math.PI * 2)) * 0.2f;
            this.rightArm.pivotZ = MathHelper.sin(this.torso.yaw) * 5.0f;
            this.rightArm.pivotX = -MathHelper.cos(this.torso.yaw) * 5.0f;
            this.rightArm.yaw += this.torso.yaw;
            this.leftArm.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0f;
            this.leftArm.pivotX = MathHelper.cos(this.torso.yaw) * 5.0f;
            this.leftArm.yaw += this.torso.yaw;
            this.leftArm.pitch += this.torso.yaw;
        }

        g = 1.0f - handSwingProgress;
        g *= g;
        g *= g;
        g = 1.0f - g;
        float h = MathHelper.sin(g * (float)Math.PI);
        float i = MathHelper.sin(handSwingProgress * (float)Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
        modelPart.pitch = (float)((double)modelPart.pitch - ((double)h * 1.2 + (double)i));
        modelPart.yaw += this.torso.yaw * 2.0f;
        modelPart.roll += MathHelper.sin(handSwingProgress * (float)Math.PI) * -0.4f;
    }

    public float fastPitch = 9f;
    public boolean side = true;

    public int test = 0;
    public long last = System.currentTimeMillis();
    public double lasta;

    private static boolean TE = false;
    private float iaa = 0;
    private boolean side2 = false;

    private void test() {
        if (TE) return;
        TE = true;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() { 
                if (iaa >= 10) side2 = true;
                if (iaa <= -10) side2 = false;

                iaa += side2 ? -2 : 2;
            } 
        }, 500, 20);
    }

    @Overwrite
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        test();
        boolean bl = ((LivingEntity)livingEntity).getRoll() > 4;
        boolean bl2 = ((LivingEntity)livingEntity).isInSwimmingPose();
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = bl ? -0.7853982f : (this.leaningPitch > 0.0f ? (bl2 ? this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982f) : this.lerpAngle(this.leaningPitch, this.head.pitch, j * ((float)Math.PI / 180))) : j * ((float)Math.PI / 180));
        this.torso.yaw = 0.0f;
        this.rightArm.pivotZ = 0.0f;
        this.rightArm.pivotX = -5.0f;
        this.leftArm.pivotZ = 0.0f;
        this.leftArm.pivotX = 5.0f;
        float k = 1.0f;
        if (bl) {
            k = (float)((Entity)livingEntity).getVelocity().lengthSquared();
            k /= 0.2f;
            k *= k * k;
        }
        if (k < 1.0f) k = 1.0f;

        if (ThalliumOptions.fastPlayerModel) {
            // ThalliumMod - Fast Player Model: Don't use cosine to calculate limb movement.
            this.leftArm.pitch = (iaa / 10) * g;
            this.rightArm.pitch = -(iaa / 10) * g;
            this.rightLeg.pitch = (iaa / 10) * 1.4f * g / k;
            this.leftLeg.pitch = -(iaa / 10) * 1.4f * g / k;
        } else {
            this.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g / k;
            this.leftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g / k;
            this.rightArm.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f / k;
            this.leftArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f / k;
        }
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.roll = 0.0f;
        this.leftLeg.roll = 0.0f;
        if (((EntityModel<?>)(Object)this).riding) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        this.rightArm.yaw = 0.0f;
        this.leftArm.yaw = 0.0f;
        this.leftArmPose.method_30156();

        boolean bl3 = ((LivingEntity)livingEntity).getMainArm() == Arm.RIGHT;
        boolean bl5 = this.leftArmPose.method_30156();
        if (bl3 != bl5) {
            this.method_30155(livingEntity);
            this.method_30154(livingEntity);
        } else {
            this.method_30154(livingEntity);
            this.method_30155(livingEntity);
        }
        this.method_29353(livingEntity, h);
        if (((BipedEntityModel<?>)(Object)this).sneaking) {
            this.torso.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.pivotZ = 4.0f;
            this.leftLeg.pivotZ = 4.0f;
            this.rightLeg.pivotY = 12.2f;
            this.leftLeg.pivotY = 12.2f;
            this.head.pivotY = 4.2f;
            this.torso.pivotY = 3.2f;
            this.leftArm.pivotY = 5.2f;
            this.rightArm.pivotY = 5.2f;
        } else {
            this.torso.pitch = 0.0f;
            this.rightLeg.pivotZ = 0.1f;
            this.leftLeg.pivotZ = 0.1f;
            this.rightLeg.pivotY = 12.0f;
            this.leftLeg.pivotY = 12.0f;
            this.head.pivotY = 0.0f;
            this.torso.pivotY = 0.0f;
            this.leftArm.pivotY = 2.0f;
            this.rightArm.pivotY = 2.0f;
        }
        CrossbowPosing.method_29350(this.rightArm, this.leftArm, h);
        this.helmet.copyPositionAndRotation(this.head);
    }

}
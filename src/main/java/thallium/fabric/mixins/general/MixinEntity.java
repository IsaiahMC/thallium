package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo ci) {
        //if (ThalliumOptions.fastItemRender && (Object)this instanceof ItemEntity) {
        //}
    }

}
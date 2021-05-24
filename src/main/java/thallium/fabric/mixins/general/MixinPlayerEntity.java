package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    private int interval_th = 80;

    /**
     * @author Inspired by Hydrinity
     * @reason Save 3ms per tick
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V"), method = "tick")
    public void improveStatsTicking(PlayerEntity e, Identifier stat) {
        if (this.age % interval_th == 0) {
            ((PlayerEntity)(Object) this).increaseStat(stat, interval_th);
        }
    }

}
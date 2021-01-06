package thallium.fabric.mixins.general;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    private int numCollisions = 0;

    /**
     * @author Based on the idea of Paper patch 137, but done completely different.
     * @reason Cap Entity collisions
     * 
     * Limit a single entity to colliding a max of configurable times per tick.
     * This will alleviate issues where living entities are hoarded in 1x1 pens
     */
    @Inject(at = @At("HEAD"), method = "tickCramming", cancellable = true)
    public void tickCramming_TH(CallbackInfo ci) {
        if (ThalliumOptions.capEntityCollisions) {
            int interval = 40; // Wait 2 seconds, unlikely all the entities are going to vanish.
            if (numCollisions <= 8 || (this.age % interval == 0)) {
                List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
                if (!list.isEmpty()) {
                    int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
                    if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                        int j = 0;
                        for (int k = 0; k < list.size(); ++k) {
                            if (list.get(k).hasVehicle()) continue;
                            ++j;
                        }
                        if (j > i - 1) this.damage(DamageSource.CRAMMING, 6.0f);
                    }
                    numCollisions = list.size();
                    for (int l = 0; l < list.size() && numCollisions < 8; ++l) {
                        Entity entity = list.get(l);
                        this.pushAway(entity);
                    }
                }
            }
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tickRiptide", cancellable = true)
    public void tickRiptide_TH(Box a, Box b, CallbackInfo ci) {
        if (numCollisions >= 8) {
            ci.cancel();
            return;
        }
    }

    @Shadow
    protected void pushAway(Entity entity) {}

}
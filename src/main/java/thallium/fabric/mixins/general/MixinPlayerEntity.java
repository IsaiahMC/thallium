package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private int sleepTimer;

    @Shadow
    public int experiencePickUpDelay;

    @Shadow
    protected boolean updateWaterSubmersionState() {return false;}

    @Shadow
    private void updateCapeAngles() {}

    @Shadow
    private ItemStack selectedItem;

    @Shadow
    protected void updateSize() {}

    @Shadow private ItemCooldownManager itemCooldownManager;

    @Shadow
    public void resetLastAttackedTicks() {
    }

    @Shadow public PlayerScreenHandler playerScreenHandler;
    @Shadow public ScreenHandler currentScreenHandler;

    public PlayerEntity getTH() {
        return (PlayerEntity)(Object) this;
    }

    /**
     * @author Inspired by Hydrinity
     * @reason Save 3ms per tick
     */
    @Overwrite
    public void tick() {
        this.noClip = getTH().isSpectator();
        if (getTH().isSpectator()) this.onGround = false;

        if (this.experiencePickUpDelay > 0) --this.experiencePickUpDelay;
        if (getTH().isSleeping()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) this.sleepTimer = 100;
            if (!getTH().world.isClient && getTH().world.isDay()) getTH().wakeUp(false, true);
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) this.sleepTimer = 0;
        }
        this.updateWaterSubmersionState();
        super.tick();

        if (!getTH().world.isClient && this.currentScreenHandler != null && !this.currentScreenHandler.canUse(getTH())) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }
        this.updateCapeAngles();
        if (!this.world.isClient) {
            getTH().getHungerManager().update(getTH());
            int interval = 80;
            if (getTH().age % interval == 0) {
                getTH().incrementStat(Stats.PLAY_ONE_MINUTE);
                if (getTH().isAlive()) getTH().increaseStat(Stats.TIME_SINCE_DEATH, interval);
                if (getTH().isSneaky()) getTH().increaseStat(Stats.SNEAK_TIME, interval);
                if (!getTH().isSleeping()) getTH().increaseStat(Stats.TIME_SINCE_REST, interval);
            }
        }

        ++this.lastAttackedTicks;
        ItemStack itemStack = this.getMainHandStack();
        if (!ItemStack.areEqual(this.selectedItem, itemStack)) {
            if (!ItemStack.areItemsEqual(this.selectedItem, itemStack))
                this.resetLastAttackedTicks();
            this.selectedItem = itemStack.copy();
        }
        int interval = 160;
        if (getTH().age % interval == 0) {
            this.updateTurtleHelmet();
            this.updateSize();
        }
        this.itemCooldownManager.update();
    }

    @Shadow
    private void updateTurtleHelmet() {}

    @Shadow
    protected void closeHandledScreen() {}

}
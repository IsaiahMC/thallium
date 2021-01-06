package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class MixinClientWorld {

    // Why does Mojang keep making new instances of Random for this one method?
    //public Random thalliumRandom = new Random();

    /*@Overwrite
    public void doRandomBlockDisplayTicks(int xCenter, int yCenter, int zCenter) {
        boolean bl = false;
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
            for (ItemStack itemStack : this.client.player.getItemsHand()) {
                if (itemStack.getItem() != Blocks.BARRIER.asItem()) continue;
                bl = true;
                break;
            }
        }
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int j = 0; j < 667; ++j) {
            this.randomBlockDisplayTick(xCenter, yCenter, zCenter, 16, thalliumRandom, bl, mutable);
            this.randomBlockDisplayTick(xCenter, yCenter, zCenter, 32, thalliumRandom, bl, mutable);
        }
    }


    @Shadow
    public void randomBlockDisplayTick(int xCenter, int yCenter, int zCenter, int i, Random random, boolean bl, Mutable mutable) {
    }*/

}
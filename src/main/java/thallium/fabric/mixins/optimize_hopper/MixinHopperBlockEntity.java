package thallium.fabric.mixins.optimize_hopper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

    @Shadow public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    @Shadow public static Inventory getInputInventory(Hopper hopper) {return null;}

    private int vanillaCompact = 0; // Allow vanilla compact. just in case I broke anything.
    private Inventory inv;

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true) 
    public void hopperTick(CallbackInfo ci) {
        // ThalliumMod - Avoids searching the hopper if there is no items inside & no items in the input
        if (ThalliumOptions.optimizeHoppers && isHopperEmpty()) {
            vanillaCompact++; 
            if (vanillaCompact < 80) {
                ci.cancel();
            } else vanillaCompact = 0;
        }
    }

    private boolean isHopperEmpty() {
        for (ItemStack i : this.inventory)
            if (!i.isEmpty()) return false;
        Inventory input = inv != null ? inv : getInputInventory((Hopper)(Object)this);
        if (null != input) inv = input;

        return null != input && input.isEmpty();
    }

}
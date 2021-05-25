package thallium.fabric.mixins.tuinity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkSectionPos;
import thallium.fabric.gui.ThalliumOptions;

/**
 * Ports Tuinity patch "0073-Use-hash-table-for-maintaining-changed-block-set.patch"
 * 
 * Original Javadoc:
 *     When a lot of block changes occur the iteration for checking can
 *     add up a bit and cause a small performance impact.
 */
@Mixin(ChunkHolder.class)
public class MixinChunkHolder {

    @Shadow public ShortSet[] blockUpdatesBySection;
    @Shadow public boolean pendingBlockUpdates;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkSectionPos;getSectionCoord(I)I"), method = "Lnet/minecraft/server/world/ChunkHolder;markForBlockUpdate(Lnet/minecraft/util/math/BlockPos;)V")
    public int test(int a) {
        int b = ChunkSectionPos.getSectionCoord(a);
        if (this.blockUpdatesBySection[b] == null) {
            this.pendingBlockUpdates = true;
            this.blockUpdatesBySection[b] = ThalliumOptions.hashtable4Blockset ? new ShortOpenHashSet(): new ShortArraySet();;
        }
        return b;
    }

}
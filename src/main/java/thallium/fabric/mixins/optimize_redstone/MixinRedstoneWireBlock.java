package thallium.fabric.mixins.optimize_redstone;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import redstone.RedstoneWireTurbo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.IRedstoneWireBlock;

@Mixin(RedstoneWireBlock.class)
public class MixinRedstoneWireBlock extends Block implements IRedstoneWireBlock {

    public MixinRedstoneWireBlock(Settings settings) {
        super(settings);
    }

    @Shadow
    public boolean wiresGivePower = true;

    @Shadow
    public static IntProperty POWER;

    @Override
    public void paper_setCanProvidePower(boolean value) {
        wiresGivePower = value;
    }

    @Override
    public boolean paper_canProvidePower() {
        return wiresGivePower;
    }

    // Paper start - Optimize redstone
    // The bulk of the new functionality is found in RedstoneWireTurbo.java
    RedstoneWireTurbo turbo = new RedstoneWireTurbo(((RedstoneWireBlock)(Object)this));

    /*
     * Modified version of pre-existing updateSurroundingRedstone, which is called from
     * this.neighborChanged and a few other methods in this class.
     * Note: Added 'source' argument so as to help determine direction of information flow
     */
    private void updateSurroundingRedstone(World worldIn, BlockPos pos, BlockState state, BlockPos source) {
        if (ThalliumOptions.optimizeRedstone) {
            turbo.updateSurroundingRedstone(worldIn, pos, state, source);
        }
        update(worldIn, pos, state);
    }

    @Shadow
    private void update(World world, BlockPos blockposition, BlockState iblockdata) {}

    @Override
    public BlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, BlockState state) {
        BlockState iblockstate = state;
        int i = state.get(POWER);
        int j = 0;
        j = this.getPower(j, worldIn.getBlockState(pos2));
        this.paper_setCanProvidePower(false);
        int k = worldIn.getReceivedRedstonePower(pos1);
        this.paper_setCanProvidePower(true);

        if (!ThalliumOptions.optimizeRedstone) {
            // This code is totally redundant to if statements just below the loop.
            if (k > 0 && k > j - 1)  j = k;
        }

        int l = 0;

        // The variable 'k' holds the maximum redstone power value of any adjacent blocks.
        // If 'k' has the highest level of all neighbors, then the power level of this
        // redstone wire will be set to 'k'.  If 'k' is already 15, then nothing inside the
        // following loop can affect the power level of the wire.  Therefore, the loop is
        // skipped if k is already 15.
        if (!ThalliumOptions.optimizeRedstone || k < 15) {
            for (Direction enumfacing : Direction.Type.HORIZONTAL) {
                BlockPos blockpos = pos1.offset(enumfacing);
                boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

                if (flag) {
                    l = this.getPower(l, worldIn.getBlockState(blockpos));
                }

                if (worldIn.getBlockState(blockpos).isSolidBlock(worldIn, blockpos) && !worldIn.getBlockState(pos1.up()).isSolidBlock(worldIn, pos1)) {
                    if (flag && pos1.getY() >= pos2.getY()) {
                        l = this.getPower(l, worldIn.getBlockState(blockpos.up()));
                    }
                } else if (!worldIn.getBlockState(blockpos).isSolidBlock(worldIn, blockpos) && flag && pos1.getY() <= pos2.getY()) {
                    l = this.getPower(l, worldIn.getBlockState(blockpos.down()));
                }
            }
        }

        if (!ThalliumOptions.optimizeRedstone) {
            // The old code would decrement the wire value only by 1 at a time.
            if (l > j) {
                j = l - 1;
            } else if (j > 0) {
                --j;
            } else j = 0;

            if (k > j - 1) j = k;
        } else {
            // The new code sets this RedstoneWire block's power level to the highest neighbor
            // minus 1.  This usually results in wire power levels dropping by 2 at a time.
            // This optimization alone has no impact on update order, only the number of updates.
            j = l - 1;

            // If 'l' turns out to be zero, then j will be set to -1, but then since 'k' will
            // always be in the range of 0 to 15, the following if will correct that.
            if (k > j) j = k;
        }

        if (i != j) {
            state = state.with(POWER, j);
            if (worldIn.getBlockState(pos1) == iblockstate)
                worldIn.setBlockState(pos1, state, 2);
        }

        return state;
    }
    // Paper end

    private int getPower(int min, BlockState iblockdata) { return Math.max(min, getPower(iblockdata)); } // Paper - Optimize redstone
    private int getPower(BlockState iblockdata) { return this.increasePower(iblockdata); } // Paper - OBFHELPER

    @Shadow
    public int increasePower(BlockState iblockdata) {
        return 0;
    }

    @Overwrite
    public void onBlockAdded(BlockState iblockdata, World world, BlockPos blockposition, BlockState iblockdata1, boolean flag) {
        if (!iblockdata1.isOf(iblockdata.getBlock()) && !world.isClient) {
            this.updateSurroundingRedstone(world, blockposition, iblockdata, null); // Paper - Optimize redstone
            Iterator<?> iterator = Direction.Type.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                Direction enumdirection = (Direction) iterator.next();
                world.updateNeighborsAlways(blockposition.offset(enumdirection), ((RedstoneWireBlock)(Object)this));
            }
            this.method_27844(world, blockposition);
        }
    }

    @Overwrite
    public void onStateReplaced(BlockState iblockdata, World world, BlockPos blockposition, BlockState iblockdata1, boolean flag) {
        if (!flag && !iblockdata.isOf(iblockdata1.getBlock())) {
            super.onStateReplaced(iblockdata, world, blockposition, iblockdata1, flag);
            if (!world.isClient) {
                Direction[] aenumdirection = Direction.values();
                int i = aenumdirection.length;
                for (int j = 0; j < i; ++j) {
                    Direction enumdirection = aenumdirection[j];
                    world.updateNeighborsAlways(blockposition.offset(enumdirection), this);
                }
                this.updateSurroundingRedstone(world, blockposition, iblockdata, null); // Paper - Optimize redstone
                this.method_27844(world, blockposition);
            }
        }
    }

    @Shadow
    public void method_27844(World world, BlockPos blockposition) {}

    @Overwrite
    public void neighborUpdate(BlockState iblockdata, World world, BlockPos blockposition, Block block, BlockPos blockposition1, boolean flag) {
        if (!world.isClient) {
            if (iblockdata.canPlaceAt(world, blockposition)) {
                this.updateSurroundingRedstone(world, blockposition, iblockdata, blockposition1); // Paper - Optimize redstone
            } else {
                dropStacks(iblockdata, world, blockposition);
                world.removeBlock(blockposition, false);
            }

        }
    }

}
package thallium.fabric.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRedstoneWireBlock {

    // Paper's OBF Helpers
    public void paper_setCanProvidePower(boolean value);
    public boolean paper_canProvidePower();
    public BlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, BlockState state);

}
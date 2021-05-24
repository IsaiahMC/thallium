package thallium.fabric.mixins.tuinity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import thallium.fabric.gui.ThalliumOptions;

@Mixin(BlockView.class)
public interface MixinBlockView {

    /**
     * @author Spottedleaf @ Tuinity
     * @reason 0065-Do-not-run-raytrace-logic-for-AIR.patch
     * 
     * Original Javadoc:
     *
     * Saves approx. 5% for the raytrace call, as most (expensive)
     * raytracing tends to go through air and returning early is an
     * easy win. The remaining problems with this function
     * are mostly with the block getting itself.
     */
    //@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockView;raycast(Lnet/minecraft/world/RaycastContext;Ljava/util/function/BiFunction;Ljava/util/function/Function;)Ljava/lang/Object;"),
    //        method = { "Lnet/minecraft/world/BlockView;raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;" })
    @Overwrite // TODO mixin does not like @Redirect for interfaces
    public default BlockHitResult raycast(RaycastContext context) {
        return BlockView.raycast(context, (contx, pos) -> {
            BlockState state = this.getBlockState((BlockPos)pos);
            if (state.isAir() && ThalliumOptions.optimizeRaytraceAir) {
                return null; // Tuinity - Return null if AIR
            }
            FluidState fluid = this.getFluidState((BlockPos)pos);
            Vec3d vec3d = contx.getStart();
            Vec3d vec3d2 = contx.getEnd();
            VoxelShape shape = contx.getBlockShape(state, (BlockView)(Object)this, (BlockPos)pos);
            BlockHitResult res = this.raycastBlock(vec3d, vec3d2, (BlockPos)pos, shape, state);
            VoxelShape shape2 = contx.getFluidShape(fluid, (BlockView)(Object) this, (BlockPos)pos);
            BlockHitResult res2 = shape2.raycast(vec3d, vec3d2, (BlockPos)pos);
            double d = res == null ? Double.MAX_VALUE : contx.getStart().squaredDistanceTo(res.getPos());
            double e = res2 == null ? Double.MAX_VALUE : contx.getStart().squaredDistanceTo(res2.getPos());
            return d <= e ? res : res2;
        }, contx -> {
            Vec3d vec3d = contx.getStart().subtract(contx.getEnd());
            return BlockHitResult.createMissed(contx.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(contx.getEnd()));
        });
    }

    @Shadow BlockHitResult raycastBlock(Vec3d a, Vec3d b, BlockPos c, VoxelShape d, BlockState s);
    @Shadow FluidState getFluidState(BlockPos pos);
    @Shadow public BlockState getBlockState(BlockPos pos);

}
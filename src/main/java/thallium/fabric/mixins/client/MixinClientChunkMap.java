/**
 * ThalliumFabric 
 * Copyright (C) 2020 Isaiah / ThalliumMod Authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package thallium.fabric.mixins.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientChunkManager.ClientChunkMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import thallium.fabric.chunk.FastChunkMap;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.IChunkMap;

@Mixin(ClientChunkMap.class)
public class MixinClientChunkMap implements IChunkMap {

    public FastChunkMap fast;

    @Final
    @Shadow
    private int radius;

    @Final
    @Shadow
    private int diameter;

    @Shadow
    private volatile int centerChunkX;
    @Shadow
    private volatile int centerChunkZ;

    @Shadow
    private int loadedChunkCount;

    private boolean reloading;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(ClientChunkManager c, int loadDistance, CallbackInfo ci) {
        if (null == fast)
            fast = new FastChunkMap(loadDistance, c);
    }

    @Override
    public FastChunkMap getFastMap() {
        return fast;
    }

    @Override
    public void setFastMap(FastChunkMap fast) {
        this.fast = fast;
    }

    @Inject(at = @At("HEAD"), method = "getChunk", cancellable = true)
    public void getChunkFast(int index, CallbackInfoReturnable<WorldChunk> ci) {
        if (ThalliumOptions.useFastRenderer)
            ci.setReturnValue(fast.getChunk(index));
    }

    @Inject(at = @At("HEAD"), method = "set", cancellable = true)
    public void fastSet(int index, WorldChunk chunk, CallbackInfo ci) {
        if (ThalliumOptions.useFastRenderer) {
            fast.set(index, chunk);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "compareAndSet", cancellable = true)
    public void compareAndSet(int index, WorldChunk expect, WorldChunk update, CallbackInfoReturnable<WorldChunk> ci) {
        if (ThalliumOptions.useFastRenderer)
            ci.setReturnValue(fast.getChunk(index));
    }

    @Inject(at = @At("HEAD"), method = "getIndex", cancellable = true)
    public void getIndex(int x, int z, CallbackInfoReturnable<Integer> ci) {
        if (ThalliumOptions.useFastRenderer)
            ci.setReturnValue((int) ChunkPos.toLong(x, z));
    }

    private ClientPlayerEntity player;

    @SuppressWarnings("resource")
    @Override
    public boolean inRadius(int chunkX, int chunkZ) {
        if (null == this.player)
            player = MinecraftClient.getInstance().player;

        boolean vanilla = Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
        if (reloading)
            return vanilla;

        int offset = ThalliumOptions.directionalRender.level;
        if (offset == -1)
            return vanilla;

        // Directional Rendering
        // Unless in F5 Third person mode, the player can't see all the chunks behind them.
        // In vanilla this is not taken into consideration causing excess chunks
        // to be "in radius" when they are not in the radius.
        switch (player.getHorizontalFacing()) {
            case NORTH:
                if (chunkZ-offset > this.centerChunkZ)
                    return false;
                break;
            case SOUTH:
                if (chunkZ+offset < this.centerChunkZ)
                    return false;
                break;
            case EAST:
                if (chunkX+offset < this.centerChunkX)
                    return false;
                break;
            case WEST:
                if (chunkX-offset > this.centerChunkX)
                    return false;
                break;
            default:
                return vanilla;
        }
        return vanilla;
    }

    @Shadow
    public WorldChunk getChunk(int index) {
        throw new IllegalArgumentException("Mixin stub");
    }

    @Override
    public WorldChunk getChunkByIndex(int index) {
        return getChunk(index);
    }

    @Override
    public void setUpdating(boolean bl) {
        this.reloading = bl;
    }

}
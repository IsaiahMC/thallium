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
package thallium.fabric.mixins.general;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientChunkManager.ClientChunkMap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import thallium.fabric.ThalliumMod;
import thallium.fabric.chunk.FastChunkMap;
import thallium.fabric.gui.ThalliumOptions;
import thallium.fabric.interfaces.IChunkMap;
import thallium.fabric.interfaces.IChunkProvider;

@Mixin(value = ClientChunkManager.class, priority=99) // priority=99 to allow Fabric API
public abstract class MixinClientChunkManager extends ChunkManager implements IChunkProvider {

    @Shadow
    public ClientWorld world;

    @Shadow
    private volatile ClientChunkMap chunks;

    @Shadow
    @Final
    private WorldChunk emptyChunk;

    @Shadow
    public static boolean positionEquals(WorldChunk chunk, int x, int y) {throw new IllegalArgumentException("Mixin Stub");}

    private int loadDistance;

    private FastChunkMap fastMap() {
        return ((IChunkMap)this.chunks).getFastMap();
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void init(ClientWorld w, int loadDistance, CallbackInfo ci) {
        ThalliumMod.RENDER_DISTANCE = loadDistance;
        this.loadDistance = loadDistance;
    }

    @Inject(at = @At("HEAD"), method = "unload", cancellable = true)
    public void unload(int x, int z, CallbackInfo ci) {
        if (ThalliumOptions.useFastRenderer) {
            ((IChunkMap)this.chunks).getFastMap().unload(x, z);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getDebugString", cancellable = true)
    public void getDebugStringThallium(CallbackInfoReturnable<String> ci) {
        if (ThalliumOptions.useFastRenderer)
            ci.setReturnValue("ThalliumChunkManager: " + ((IChunkMap)this.chunks).getFastMap().fastChunks.size());
    }

    @Override
    public BlockView getWorld() {
        return world;
    }

    /**
     * Replace instances of ClientChunkMap in the vanilla method with our FastChunkMap
     */
    @Inject(at = @At("HEAD"), method = "updateLoadDistance", cancellable = true)
    public void updateLoadDistanceFast(int loadDistance, CallbackInfo ci) {
        this.loadDistance = (ThalliumMod.RENDER_DISTANCE = loadDistance);
        if (ThalliumOptions.useFastRenderer) {
            FastChunkMap clientChunkMap = new FastChunkMap(Math.max(2, loadDistance) + 3, (ClientChunkManager)(Object)this);
            ((IChunkMap)this.chunks).setUpdating(true);
            ((IChunkMap)this.chunks).getFastMap().fastChunks.forEach((longKey,worldChunk) -> {
                if (((IChunkMap)this.chunks).inRadius(ChunkPos.getPackedX(longKey), ChunkPos.getPackedZ(longKey)))
                    clientChunkMap.set(longKey, worldChunk);
            });
            ((IChunkMap)this.chunks).setFastMap(clientChunkMap);
            ((IChunkMap)this.chunks).setUpdating(false);
            ci.cancel();
        }
    }

    /**
     * Replace instance of AtomicReference.
     */
    @Inject(at = @At("HEAD"), method = "loadChunkFromPacket", cancellable = true)
    public void loadChunkFromPacketFast(int x, int z, BiomeArray biomes, PacketByteBuf buf, CompoundTag tag, int i, boolean bl, CallbackInfoReturnable<WorldChunk> ci) {
        if (ThalliumOptions.useFastRenderer) {
            long j = ChunkPos.toLong(x, z);
            WorldChunk worldChunk = (WorldChunk)fastMap().getChunk(j); // ThalliumMod - Replace vanilla AtomicReference call
            if (bl || !positionEquals(worldChunk, x, z)) {
                if (biomes == null) {
                    ThalliumMod.LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", x, z);
                    ci.setReturnValue(null);
                }
                worldChunk = new WorldChunk(this.world, new ChunkPos(x, z), biomes);
                worldChunk.loadFromPacket(biomes, buf, tag, i);
            } else worldChunk.loadFromPacket(biomes, buf, tag, i);
            fastMap().set(j, worldChunk);
    
            // copied from vanilla
            ChunkSection[] chunkSections = worldChunk.getSectionArray();
            LightingProvider lightingProvider = this.getLightingProvider();
            lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
            for (int k = 0; k < chunkSections.length; ++k) {
                ChunkSection chunkSection = chunkSections[k];
                lightingProvider.setSectionStatus(ChunkSectionPos.from(x, k, z), ChunkSection.isEmpty(chunkSection));
            }
            this.world.resetChunkColor(x, z);
            ci.setReturnValue(worldChunk);
        }
    }

    @Override
    @Overwrite
    public Chunk getChunk(int i, int j, ChunkStatus chunkStatus, boolean bl) {
        if (ThalliumOptions.useFastRenderer) {
            WorldChunk worldChunk = fastMap().getChunk(ChunkPos.toLong(i, j));
            if (((IChunkMap)this.chunks).inRadius(i, j) && positionEquals(worldChunk, i, j))
                return worldChunk;
            return (bl ? this.emptyChunk : null);
        } else {
            int diameter = loadDistance * 2 + 1;
            int abc = Math.floorMod(j, diameter) * diameter + Math.floorMod(i, diameter);
            WorldChunk worldChunk = ((IChunkMap)this.chunks).getChunkByIndex(abc);
            if (((IChunkMap)this.chunks).inRadius(i, j) && positionEquals(worldChunk, i, j))
                return worldChunk;
            return bl ? this.emptyChunk : null;
        }
    }

    @Override
    public void refreshRender() {
        ((ClientChunkManager)(Object)this).updateLoadDistance(this.loadDistance);
    }

}

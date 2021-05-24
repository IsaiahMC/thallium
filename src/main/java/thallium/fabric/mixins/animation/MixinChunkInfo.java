package thallium.fabric.mixins.animation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.render.WorldRenderer.ChunkInfo;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkBuilder.ChunkData;
import net.minecraft.util.math.Direction;
import thallium.fabric.interfaces.IChunkInfo;

@Mixin(ChunkInfo.class)
public class MixinChunkInfo implements IChunkInfo {

    @Shadow
    private ChunkBuilder.BuiltChunk chunk;

    @Shadow
    private Direction direction;

    /**
     * Gets the built chunk
     * 
     * @author ThalliumMod
     */
    @Override
    public ChunkBuilder.BuiltChunk getBuiltChunk() {
        return chunk;
    }

    /**
     * Get the built chunk's ChunkData
     * 
     * @author ThalliumMod
     */
    @Override
    public ChunkData getBuiltChunkData() {
        return chunk.getData();
    }

}
package thallium.fabric.interfaces;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkBuilder.ChunkData;

public interface IChunkInfo {

    public ChunkBuilder.BuiltChunk getBuiltChunk();

    /**
     * Get the built chunk's ChunkData
     */
    public ChunkData getBuiltChunkData();


}

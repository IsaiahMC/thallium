package thallium.fabric.interfaces;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;

public class CurrentChunkData {

    public static final ThreadLocal<BuiltChunk> CURRENT_CHUNK_DATA = new ThreadLocal<>();
    //public static BuiltChunk CURRENT_CHUNK_DATA = null;

}
package thallium.fabric.interfaces;

import net.minecraft.world.chunk.WorldChunk;
import thallium.fabric.chunk.FastChunkMap;

public interface IChunkMap {

    FastChunkMap getFastMap();

    void setFastMap(FastChunkMap fast);

    WorldChunk getChunkByIndex(int index);

    void setUpdating(boolean bl);

    boolean inRadius(int chunkX, int chunkZ);

}

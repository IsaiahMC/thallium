package thallium.fabric.mixins.animation;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.texture.Sprite;
import thallium.fabric.interfaces.IChunkData;

@Mixin(ChunkBuilder.ChunkData.class)
public class MixinChunkData implements IChunkData {

    public Set<Sprite> visibleTextures = new HashSet<>();

    @Override
    public Set<Sprite> getVisibleTextures() {
        return visibleTextures;
    }

}
package thallium.fabric.interfaces;

import java.util.Set;

import net.minecraft.client.texture.Sprite;

public interface IChunkData {

    Set<Sprite> getVisibleTextures();

}
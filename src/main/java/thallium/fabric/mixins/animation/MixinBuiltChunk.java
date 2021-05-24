package thallium.fabric.mixins.animation;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkBuilder.BuiltChunk;
import net.minecraft.client.texture.Sprite;
import thallium.fabric.interfaces.CurrentChunkData;
import thallium.fabric.interfaces.IChunkData;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class MixinBuiltChunk implements IChunkData {

    @Shadow
    public ChunkBuilder.ChunkData getData() {return null;}

    @Inject(method = "rebuild", at = @At("HEAD"))
    private void onRebuild(CallbackInfo ci) {
        CurrentChunkData.CURRENT_CHUNK_DATA.set((BuiltChunk)(Object)this);
    }

    public Set<Sprite> visibleTextures = new HashSet<>();

    @Override
    public Set<Sprite> getVisibleTextures() {
        return visibleTextures;
    }

}
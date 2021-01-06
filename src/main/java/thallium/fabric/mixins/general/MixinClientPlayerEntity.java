package thallium.fabric.mixins.general;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public ClientPlayNetworkHandler networkHandler;

    @Shadow
    public List<ClientPlayerTickable> tickables;

    @Shadow
    public Input input;

    public int abc = 0;

    /**
     * Improves player tick. Decreases isChunkLoaded from .40% of tick to .20%.
     * Also is more descriptive with the profiler
     * 
     * @reason Don't create new BlockPos objects when calling isChunkLoaded
     * @author ThalliumMod
     */
    @Overwrite
    public void tick() {
        boolean doRE = false;
        MinecraftClient.getInstance().getProfiler().push("TH-isChunkLoaded");
        if (!this.world.isChunkLoaded((int)this.getX() >> 4, (int)this.getZ() >> 4)) doRE = true;
        MinecraftClient.getInstance().getProfiler().pop();
        if (doRE) return;

        MinecraftClient.getInstance().getProfiler().push("TH-SuperTick");
        super.tick();
        MinecraftClient.getInstance().getProfiler().pop();

        MinecraftClient.getInstance().getProfiler().push("TH-Movement");
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
            }
        } else this.sendMovementPackets();
        MinecraftClient.getInstance().getProfiler().pop();

        MinecraftClient.getInstance().getProfiler().push("TH-PlayerTickable");
        for (ClientPlayerTickable clientPlayerTickable : this.tickables)
            clientPlayerTickable.tick();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    @Shadow
    public void sendMovementPackets() {
    }

}
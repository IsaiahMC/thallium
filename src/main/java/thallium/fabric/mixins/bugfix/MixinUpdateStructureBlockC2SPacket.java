package thallium.fabric.mixins.bugfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;

/**
 * Fix MC-148809
 */
@Mixin(UpdateStructureBlockC2SPacket.class)
public class MixinUpdateStructureBlockC2SPacket {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readString(I)Ljava/lang/String;"), method = { "read" })
    public String thallium_fix_MC_148809(PacketByteBuf buf, int len) {
        return buf.readString(len == 12 ? 128 : len);
    }

}

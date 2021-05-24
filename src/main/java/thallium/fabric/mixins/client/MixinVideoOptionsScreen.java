package thallium.fabric.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import thallium.fabric.gui.ThalliumOptionsScreen;

@Mixin(VideoOptionsScreen.class)
public class MixinVideoOptionsScreen extends GameOptionsScreen {

    protected MixinVideoOptionsScreen(Text title) {
        super(null, null, title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    public void addCustomButton(CallbackInfo ci) {
        this.addButton(new ButtonWidget(this.width / 2 + 110, this.height - 27, 90, 20, new LiteralText("Thallium Options"), button -> {
            this.client.openScreen(new ThalliumOptionsScreen(this));
        }));
    }

}
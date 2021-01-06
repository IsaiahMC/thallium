package thallium.fabric.gui;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;

public class ThalliumOptionsScreen extends Screen {

    private static final Option[] OPTIONS = new Option[]{ThalliumOptions.FAST_RENDER, ThalliumOptions.FAST_MATH, ThalliumOptions.OPTIMIZE_ANIMATIONS,
            ThalliumOptions.RENDER_SKIP, ThalliumOptions.DIRECTIONAL_RENDER, ThalliumOptions.FAST_MATH_TYPE, ThalliumOptions.PLR_MODEL_OPTIMIZE, ThalliumOptions.HOPPER_OPTIMIZE,
            ThalliumOptions.NO_FOG, ThalliumOptions.BLOCK_TICK_OPTIMIZE, ThalliumOptions.FAST_ITEM_RENDER, ThalliumOptions.OPTIMIZE_REDSTONE,
            ThalliumOptions.OPTIMIZE_ENTITY_COLLISION};

    private List<? extends OrderedText> tooltipList;
    private ButtonListWidget list;
    private Screen parent;

    public ThalliumOptionsScreen(Screen parent) {
        super(new LiteralText("Thallium Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);

        this.list.addAll(OPTIONS);
        this.children.add(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> {
            this.client.options.write();
            this.client.getWindow().applyVideoMode();
            this.client.openScreen(this.parent);
        }));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button))
            return true;
        if (this.list.mouseReleased(mouseX, mouseY, button))
            return true;
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltipList = null;
        Optional<AbstractButtonWidget> optional = this.list.getHoveredButton(mouseX, mouseY);
        if (optional.isPresent() && optional.get() instanceof OptionButtonWidget)
            ((OptionButtonWidget)optional.get()).getOption().getTooltip().ifPresent(list -> this.tooltipList = list);

        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        try {
            // 1.16.2 Made some breaking changes to the GUI title
            // We put this method inside a try-block to keep compact with 1.16.1
            DrawableHelper.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        } catch (Exception e) {/* 1.16.1 */}

        super.render(matrices, mouseX, mouseY, delta);

        try {
            if (this.tooltipList != null)
                this.renderOrderedTooltip(matrices, this.tooltipList, mouseX, mouseY);
        } catch (Exception e) {/* 1.16.1 */}
    }
}


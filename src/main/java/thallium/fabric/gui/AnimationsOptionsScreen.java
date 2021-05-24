package thallium.fabric.gui;

import static thallium.fabric.gui.ThalliumOptions.*;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;

public class AnimationsOptionsScreen extends Screen {

    // Animations
    public static final BooleanOption ANIMATE_WATER = new BooleanOption("Water Animated", gameOptions -> animateWater, (gameOptions, boolean_) -> {
        animateWater = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_LAVA = new BooleanOption("Lava Animated", gameOptions -> animateLava, (gameOptions, boolean_) -> {
        animateLava = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_FIRE = new BooleanOption("Fire Animated", gameOptions -> animateFire, (gameOptions, boolean_) -> {
        animateFire = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_PORTAL = new BooleanOption("Portal Animated", gameOptions -> animatePortal, (gameOptions, boolean_) -> {
        animatePortal = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_PRISMARINE = new BooleanOption("Prismarine Animated", gameOptions -> animatePrismarine, (gameOptions, boolean_) -> {
        animatePrismarine = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_TEXTURES = new BooleanOption("Textures Animated", gameOptions -> animateTextures, (gameOptions, boolean_) -> {
        animateTextures = boolean_;
        save();
    });

    public static final BooleanOption ANIMATE_CMD = new BooleanOption("Command Block Animated", gameOptions -> animateCmdBlock, (gameOptions, boolean_) -> {
        animateCmdBlock = boolean_;
        save();
    });

    // Options List
    private static final Option[] OPTIONS = new Option[]{ANIMATE_WATER, ANIMATE_LAVA, ANIMATE_FIRE, ANIMATE_PORTAL, ANIMATE_PRISMARINE, ANIMATE_CMD, ANIMATE_TEXTURES};

    private List<? extends OrderedText> tooltipList;
    private ButtonListWidget list;
    private Screen parent;

    public AnimationsOptionsScreen(Screen parent) {
        super(new LiteralText(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 75, 16, 150, 20, new LiteralText("Optimize Animations: " + (optimizeAnimations ? "ON" : "OFF")), button -> {
            optimizeAnimations = !optimizeAnimations;
            button.setMessage(new LiteralText("Optimize Animations: " + (optimizeAnimations ? "ON" : "OFF")));
            save();
        }));
        this.list = new ButtonListWidget(this.client, this.width, this.height, 42, this.height - 32, 25);

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
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        if (this.list.mouseReleased(mouseX, mouseY, button)) return true;
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
        super.render(matrices, mouseX, mouseY, delta);

        if (this.tooltipList != null)
            this.renderOrderedTooltip(matrices, this.tooltipList, mouseX, mouseY);
    }
}


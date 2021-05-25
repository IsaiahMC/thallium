package thallium.fabric.gui;

import java.util.List;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import thallium.fabric.math.FastMathType;

import static thallium.fabric.gui.ThalliumOptions.*;

public class ThalliumOptionsScreen extends Screen {

    public static final BooleanOption FAST_RENDER = new BooleanOption("Use fast renderer", gameOptions -> useFastRenderer, (gameOptions, boolean_) -> {
        useFastRenderer = boolean_; // TODO: Fix problems with disabling fast render
        save();
    });

    public static final BooleanOption FAST_MATH = new BooleanOption("Use fast math", gameOptions -> useFastMath, (gameOptions, boolean_) -> {
        useFastMath = boolean_;
        save();
    });

    public static final BooleanOption OPTIMIZE_ANIMATIONS = new BooleanOption("Optimize animations", gameOptions -> optimizeAnimations, (gameOptions, boolean_) -> {
        optimizeAnimations = boolean_;
        save();
    });

    public static final BooleanOption PLR_MODEL_OPTIMIZE = new BooleanOption("Fast Entity Model", gameOptions -> fastPlayerModel, (gameOptions, boolean_) -> {
        fastPlayerModel = boolean_;
        save();
    });

    public static final BooleanOption HOPPER_OPTIMIZE = new BooleanOption("Optimize Hopper Tick", gameOptions -> optimizeHoppers, (gameOptions, boolean_) -> {
        optimizeHoppers = boolean_;
        save();
    });

    public static final BooleanOption BLOCK_TICK_OPTIMIZE = new BooleanOption("Optimize Block Tick", gameOptions -> optimizeBlockTick, (gameOptions, boolean_) -> {
        optimizeBlockTick = boolean_;
        save();
    });

    public static final BooleanOption FAST_ITEM_RENDER = new BooleanOption("Fast Item Render", gameOptions -> fastItemRender, (gameOptions, boolean_) -> {
        fastItemRender = boolean_;
        save();
    });

    public static final BooleanOption OPTIMIZE_REDSTONE = new BooleanOption("Optimize Redstone", gameOptions -> optimizeRedstone, (gameOptions, boolean_) -> {
        optimizeRedstone = boolean_;
        save();
    });

    public static final BooleanOption OPTIMIZE_ENTITY_COLLISION = new BooleanOption("Optimize Entity Collision", gameOptions -> capEntityCollisions, (gameOptions, boolean_) -> {
        capEntityCollisions = boolean_;
        save();
    });
    
    public static final BooleanOption NO_RAYTRACE_AIR = new BooleanOption("Optimise air in raytrace", gameOptions -> optimizeRaytraceAir, (gameOptions, boolean_) -> {
        optimizeRaytraceAir = boolean_;
        save();
    });

    public static final BooleanOption HT_BLSET = new BooleanOption("Hashtable for block set", gameOptions -> hashtable4Blockset, (gameOptions, boolean_) -> {
        hashtable4Blockset = boolean_;
        save();
    });

    public static CyclingOption DIRECTIONAL_RENDER;
    public static CyclingOption FAST_MATH_TYPE;
    public static DoubleOption DOUBLE_TEST;
    public static CyclingOption NO_FOG;
    public static CyclingOption ANIMATION_SCREEN;

    public static void init_th() {
        DIRECTIONAL_RENDER = new CyclingOption("Directional Render", (options,integer) -> {
            directionalRender = directionalRender.ordinal() >= EnumDirectionalRendering.values().length-1 ? EnumDirectionalRendering.OFF :
                EnumDirectionalRendering.values()[directionalRender.ordinal()+1];
            save();
        }, (options,cyc) -> { return new LiteralText("Directional Render: " + directionalRender.name()); });

        FAST_MATH_TYPE = new CyclingOption("Math Algorithm", (options,integer) -> {
            fastMathType = fastMathType.ordinal() >= FastMathType.values().length-1 ? FastMathType.VANILLA : FastMathType.values()[fastMathType.ordinal()+1];
            save();
        }, (options,cyc) -> { return new LiteralText("Math Algorithm: " + (useFastMath ? fastMathType.name() : "Fast Math OFF")); });

        NO_FOG = new CyclingOption("Fog Type", (options,integer) -> {
            fogType = fogType.ordinal() >= EnumFogType.values().length-1 ? EnumFogType.NORMAL : EnumFogType.values()[fogType.ordinal()+1];
            save();
        }, (options,cyc) -> { return new LiteralText("Fog: " + fogType.name()); });

        ANIMATION_SCREEN = new CyclingOption("Animations", (options,integer) -> {
            MinecraftClient.getInstance().openScreen(new AnimationsOptionsScreen(ThalliumOptionsScreen.INSTANCE));
        }, (options,cyc) -> { return new LiteralText("Animations..."); });
    }

    private List<? extends OrderedText> tooltipList;
    private ButtonListWidget list;
    private Screen parent;
    public static Screen INSTANCE;

    public ThalliumOptionsScreen(Screen parent) {
        super(new LiteralText("Thallium Options"));
        this.parent = parent;
        INSTANCE = this;
    }

    @Override
    protected void init() {
        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);

        Option[] OPTIONS = new Option[]{FAST_RENDER, FAST_MATH,
                        DIRECTIONAL_RENDER, FAST_MATH_TYPE, PLR_MODEL_OPTIMIZE, HOPPER_OPTIMIZE,
                        NO_FOG, BLOCK_TICK_OPTIMIZE, FAST_ITEM_RENDER, OPTIMIZE_REDSTONE,
                        OPTIMIZE_ENTITY_COLLISION, ANIMATION_SCREEN, NO_RAYTRACE_AIR, HT_BLSET};

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


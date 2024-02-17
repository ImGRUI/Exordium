package dev.tr7zw.exordium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import net.minecraft.network.chat.Component;
import dev.tr7zw.exordium.util.BufferRenderer;
import dev.tr7zw.exordium.util.DelayedRenderCallManager;
import dev.tr7zw.exordium.util.NametagScreenBuffer;
import net.minecraft.client.gui.screens.Screen;

public abstract class ExordiumModBase {

    public static ExordiumModBase instance;
    private static boolean forceBlend, blendBypass;
    private static int bypassTurnoff;

    public Config config;
    private final File settingsFile = new File("config", "exordium.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private BufferRenderer screenBufferRenderer;
    private NametagScreenBuffer nametagScreenBuffer;
    private RenderTarget temporaryScreenOverwrite = null;
    public static SignSettings signSettings = new SignSettings();
    private final DelayedRenderCallManager delayedRenderCallManager = new DelayedRenderCallManager();

    public void onInitialize() {
		instance = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(Files.readString(settingsFile.toPath(), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if(ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
		initModloader();
	}
	
    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.writeString(settingsFile.toPath(), gson.toJson(config));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public BufferRenderer getScreenBufferRenderer() {
        if(screenBufferRenderer == null) {
            screenBufferRenderer = new BufferRenderer(true);
        }
        return screenBufferRenderer;
    }
    
    public NametagScreenBuffer getNameTagScreenBuffer() {
        if(nametagScreenBuffer == null) {
            nametagScreenBuffer = new NametagScreenBuffer(1000/config.targetFPSNameTags);
        }
        return nametagScreenBuffer;
    }
    
    public DelayedRenderCallManager getDelayedRenderCallManager() {
        return delayedRenderCallManager;
    }
    
    public abstract void initModloader();
    public Screen createConfigScreen(Screen parent){
        YetAnotherConfigLib.Builder screen = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("text.exordium.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("text.exordium.title"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("text.exordium.enableGui"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.enableGui.tooltip")).build())
                                .binding(true, () -> config.enabledGui, newVal -> config.enabledGui = newVal)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.createBuilder(Integer.class)
                                .name(Component.translatable("text.exordium.targetFramerateGui"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.targetFramerateGui.tooltip")).build())
                                .binding(20, () -> config.targetFPSIngameGui, newVal -> config.targetFPSIngameGui = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(5, 60)
                                        .step(1))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("text.exordium.enabledGuiAnimationSpeedup"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.enabledGuiAnimationSpeedup.tooltip")).build())
                                .binding(true, () -> config.enabledGuiAnimationSpeedup, newVal -> config.enabledGuiAnimationSpeedup = newVal)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.createBuilder(Integer.class)
                                .name(Component.translatable("text.exordium.targetFPSIngameGuiAnimated"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.targetFPSIngameGuiAnimated.tooltip")).build())
                                .binding(60, () -> config.targetFPSIngameGuiAnimated, newVal -> config.targetFPSIngameGuiAnimated = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(30, 120)
                                        .step(1))
                                .build())//
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("text.exordium.enableSignBuffering"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.enableSignBuffering.tooltip")).build())
                                .binding(false, () -> config.enableSignBuffering, newVal -> config.enableSignBuffering = newVal)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("text.exordium.enableNametagScreenBuffering"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.enableNametagScreenBuffering.tooltip")).build())
                                .binding(false, () -> config.enableNametagScreenBuffering, newVal -> config.enableNametagScreenBuffering = newVal)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.createBuilder(Integer.class)
                                .name(Component.translatable("text.exordium.targetFPSNameTags"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("text.exordium.targetFPSNameTags.tooltip")).build())
                                .binding(60, () -> config.targetFPSNameTags, newVal -> config.targetFPSNameTags = newVal)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(30, 80)
                                        .step(1))
                                .build())
                        .build())
                .save(this::writeConfig);
        return screen.build().generateScreen(parent);
    }

    public static boolean isForceBlend() {
        return forceBlend;
    }

    public static void setForceBlend(boolean forceBlend) {
        ExordiumModBase.forceBlend = forceBlend;
    }

    public static boolean isBlendBypass() {
        return !blendBypass;
    }

    public static void setBlendBypass(boolean blendBypass) {
        // force blend is on, bypass is on and we are turning it off
        if(forceBlend && ExordiumModBase.blendBypass && !blendBypass) {
            correctBlendMode(); // fix the blend state to the expected one
        }
        ExordiumModBase.blendBypass = blendBypass;
    }
    
    public static int getBypassTurnoff() {
        return bypassTurnoff;
    }

    public static void setBypassTurnoff(int bypassTurnoff) {
        ExordiumModBase.bypassTurnoff = bypassTurnoff;
    }

    public static void correctBlendMode() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public RenderTarget getTemporaryScreenOverwrite() {
        return temporaryScreenOverwrite;
    }

    public void setTemporaryScreenOverwrite(RenderTarget temporaryScreenOverwrite) {
        this.temporaryScreenOverwrite = temporaryScreenOverwrite;
    }
    
}

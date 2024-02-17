package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.util.BufferRenderer;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@Mixin(value= Gui.class, priority = 1500) // higher priority, so it also captures rendering happening at RETURN
public class GuiMixin{
    @Unique
    private ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    @Unique
    private BufferRenderer bufferRenderer = new BufferRenderer();
    @Unique
    private boolean reRenderCrosshair = false;
    
    @Final
    @Shadow
    private Minecraft minecraft;
    @Final
    @Shadow
    private ChatComponent chat;
    @Shadow
    private Component title;
    @Shadow
    private int titleTime;
    @Shadow
    private int titleFadeInTime;
    @Shadow
    private int titleStayTime;
    @Shadow
    private int titleFadeOutTime;
    @Shadow
    private int tickCount;
    @Shadow
    private Component overlayMessageString;
    @Shadow
    private int overlayMessageTime;
    @Shadow
    private int toolHighlightTimer;
    
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/Minecraft;getDeltaFrameTime()F"), cancellable = true)
    public void render(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui) {
            return;
        }
        boolean cancel = bufferRenderer.render();
        if(cancel) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            renderCrosshair(guiGraphics);
            RenderSystem.defaultBlendFunc();
            ci.cancel();
        }
    }
    
    @Inject(method = "render", at = @At("RETURN"))
    public void renderEnd(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui) {
            return;
        }
        int targetFps = ExordiumModBase.instance.config.targetFPSIngameGui;
        if(ExordiumModBase.instance.config.enabledGuiAnimationSpeedup) {
            // Item name tooltip
            if(toolHighlightTimer > 0 && toolHighlightTimer < 15) {
                targetFps = ExordiumModBase.instance.config.targetFPSIngameGuiAnimated;
            }
            // title/subtitle
            if (this.title != null && this.titleTime > 0) {
                int m = 255;
                float j = this.titleTime - f;
                if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
                    float p = (this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - j;
                    m = (int) (p * 255.0F / this.titleFadeInTime);
                }
                if (this.titleTime <= this.titleFadeOutTime)
                    m = (int) (j * 255.0F / this.titleFadeOutTime);
                m = Mth.clamp(m, 0, 255);
                if (m != 255) {
                    targetFps = ExordiumModBase.instance.config.targetFPSIngameGuiAnimated;
                }
            }
            // Attack indicator
            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                assert this.minecraft.player != null;
                float j = this.minecraft.player.getAttackStrengthScale(0.0F);
                if(j < 1.0F) {
                    targetFps = ExordiumModBase.instance.config.targetFPSIngameGuiAnimated;
                }
            }
            // Chat
            ChatAccess chatAccess = (ChatAccess) chat;
            if(chatAccess.exordium$hasActiveAnimations(tickCount)) {
                targetFps = ExordiumModBase.instance.config.targetFPSIngameGuiAnimated;
            }
            // Overlay message "Actionbar"
            if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
                this.minecraft.getProfiler().push("overlayMessage");
                float timerj = this.overlayMessageTime - f;
                int m = (int) (timerj * 255.0F / 20.0F);
                if (m > 255)
                    m = 255;
                if (m > 8 && m != 255) {
                    targetFps = ExordiumModBase.instance.config.targetFPSIngameGuiAnimated;
                }
                this.minecraft.getProfiler().pop();
            }
        }
        bufferRenderer.renderEnd(1000/targetFps);
        if(reRenderCrosshair) {
            reRenderCrosshair = false;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            renderCrosshair(guiGraphics);
            RenderSystem.defaultBlendFunc();
        }
    }
   
    @Shadow
    private void renderCrosshair(GuiGraphics guiGraphics) {

    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(ExordiumModBase.instance.config.enabledGui && bufferRenderer.isRendering()) {
            reRenderCrosshair = true;
            ci.cancel();
        }
    }
    
    // Fix for AppleSkin

    @Inject(method = "renderPlayerHealth", at = @At("HEAD"))
    private void renderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderPlayerHealth", at = @At("RETURN"))
    private void renderPlayerHealthReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // NEW
    @Inject(method = "renderSelectedItemName", at = @At("HEAD"))
    private void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderSelectedItemName", at = @At("RETURN"))
    private void renderSelectedItemNameReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // NEW
    @Inject(method = "renderSavingIndicator", at = @At("HEAD"))
    private void renderSavingIndicator(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderSavingIndicator", at = @At("RETURN"))
    private void renderSavingIndicatorReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // NEW
    @Inject(method = "renderEffects", at = @At("HEAD"))
    private void renderEffects(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderEffects", at = @At("RETURN"))
    private void renderEffectsReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // NEW
    @Inject(method = "renderJumpMeter", at = @At("HEAD"))
    private void renderJumpMeter(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderJumpMeter", at = @At("RETURN"))
    private void renderJumpMeterReturn(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // NEW
    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    private void renderExperienceBar(GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderExperienceBar", at = @At("RETURN"))
    private void renderExperienceBarReturn(GuiGraphics guiGraphics, int i, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    // Fix Scoreboard overlapping with overlays like spyglass
    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"))
    private void displayScoreboardSidebar(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("RETURN"))
    private void displayScoreboardSidebarReturn(GuiGraphics guiGraphics, Objective objective, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }

}

package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.components.DebugScreenOverlay;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    // NEW
    @Inject(method = "renderLines", at = @At("HEAD"))
    private void renderLines(GuiGraphics guiGraphics, List<String> list, boolean bl, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderLines", at = @At("RETURN"))
    private void renderLinesReturn(GuiGraphics guiGraphics, List<String> list, boolean bl, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    
}

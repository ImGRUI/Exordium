package dev.tr7zw.exordium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.tr7zw.exordium.ExordiumModBase;

@Mixin(SubtitleOverlay.class)
public class SuntitleOverlayMixin {

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
    
}

package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.tr7zw.exordium.ExordiumModBase;

@Mixin(SpectatorGui.class)
public class SpectatorGuiMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"))
    public void renderHotbar(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    public void renderHotbarEnd(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    //
    @Inject(method = "renderPage", at = @At("HEAD"))
    public void renderPage(GuiGraphics guiGraphics, float f, int i, int j, SpectatorPage spectatorPage, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderPage", at = @At("TAIL"))
    public void renderPageEnd(GuiGraphics guiGraphics, float f, int i, int j, SpectatorPage spectatorPage, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    //
    @Inject(method = "renderSlot", at = @At("HEAD"))
    public void renderSlot(GuiGraphics guiGraphics, int i, int j, float f, float g, SpectatorMenuItem spectatorMenuItem, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderSlot", at = @At("TAIL"))
    public void renderSlotEnd(GuiGraphics guiGraphics, int i, int j, float f, float g, SpectatorMenuItem spectatorMenuItem, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    //
    @Inject(method = "renderTooltip", at = @At("HEAD"))
    public void renderTooltip(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderTooltip", at = @At("TAIL"))
    public void renderTooltipEnd(GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    
}

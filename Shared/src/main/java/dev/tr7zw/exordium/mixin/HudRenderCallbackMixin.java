package dev.tr7zw.exordium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.tr7zw.exordium.ExordiumModBase;

@Mixin(HudRenderCallback.class)
public class HudRenderCallbackMixin {

    @Inject(method = "onHudRender", at = @At("HEAD"))
    public void onHudRender(GuiGraphics par1, float par2, CallbackInfo ci) {
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }
    
    @Inject(method = "onHudRender", at = @At("TAIL"))
    public void onHudRenderEnd(GuiGraphics par1, float par2, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    
}

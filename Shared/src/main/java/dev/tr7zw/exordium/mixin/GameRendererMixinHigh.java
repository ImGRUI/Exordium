package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(value = GameRenderer.class, priority = 1500) //needs to be higher to also capture Architectury for REI
public class GameRendererMixinHigh {

    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "render(FJZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderScreenPost(float f, long l, boolean bl, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledScreensLegacy || minecraft.level == null) {
            return;
        }
        ExordiumModBase.instance.getScreenBufferRenderer().renderEnd(1000/ExordiumModBase.instance.config.targetFPSIngameScreens);
    }

}

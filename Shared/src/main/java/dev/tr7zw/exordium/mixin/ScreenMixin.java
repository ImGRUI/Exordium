package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.tr7zw.exordium.ExordiumModBase;

import java.util.List;

@Mixin(GuiGraphics.class)
public class ScreenMixin {

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"))
    public void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }
    
    @Inject(method = "renderTooltipInternal", at = @At("TAIL"))
    public void renderTooltipInternalEnd(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    
}

package dev.tr7zw.exordium.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;

/**
 * FIXME: this is just applying to the inventory player. Find a more general way that works for 
 *
 */
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private static void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(true);
    }
    // OLD
    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"))
    private static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int i, int j, int k, float f, float g, LivingEntity livingEntity, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(true);
    }
    
    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("RETURN"))
    private static void renderEntityInInventoryFollowsMouseReturn(GuiGraphics guiGraphics, int i, int j, int k, float f, float g, LivingEntity livingEntity, CallbackInfo ci) {
        ExordiumModBase.setBlendBypass(false);
    }
    
}

package dev.tr7zw.exordium.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.tr7zw.exordium.ExordiumModBase;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(GuiGraphics guiGraphics, int i, Scoreboard scoreboard, @Nullable Objective objective, CallbackInfo ci) {
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }
    
    @Inject(method = "render", at = @At("TAIL"))
    public void renderEnd(GuiGraphics guiGraphics, int i, Scoreboard scoreboard, @Nullable Objective objective, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
        RenderSystem.defaultBlendFunc();
    }
    //
    @Inject(method = "renderPingIcon", at = @At("HEAD"))
    private void renderPingIcon(GuiGraphics guiGraphics, int i, int j, int k, PlayerInfo playerInfo, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderPingIcon", at = @At("RETURN"))
    private void renderPingIconReturn(GuiGraphics guiGraphics, int i, int j, int k, PlayerInfo playerInfo, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    //
    @Inject(method = "renderTablistScore", at = @At("HEAD"))
    private void renderTablistScore(Objective objective, int i, String string, int j, int k, UUID uUID, GuiGraphics guiGraphics, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderTablistScore", at = @At("RETURN"))
    private void renderTablistScoreReturn(Objective objective, int i, String string, int j, int k, UUID uUID, GuiGraphics guiGraphics, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    //
    @Inject(method = "renderTablistHearts", at = @At("HEAD"))
    private void renderTablistHearts(int i, int j, int k, UUID uUID, GuiGraphics guiGraphics, int l, CallbackInfo ci) {
        if(!ExordiumModBase.instance.config.enabledGui)
            return;
        ExordiumModBase.correctBlendMode();
        ExordiumModBase.setForceBlend(true);
    }

    @Inject(method = "renderTablistHearts", at = @At("RETURN"))
    private void renderTablistHeartsReturn(int i, int j, int k, UUID uUID, GuiGraphics guiGraphics, int l, CallbackInfo ci) {
        ExordiumModBase.setForceBlend(false);
    }
    
}

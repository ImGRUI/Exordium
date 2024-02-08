package dev.tr7zw.exordium.mixin;

import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.exordium.access.SignBufferHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import dev.tr7zw.exordium.util.SignBufferRendererTest;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBufferHolder {

    private SignBufferRendererTest cachedBufferRenderer = null;
    private SignText front;
    private SignText back;
    private int currentLight = -1;

    @Override
    public boolean renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean bl, int light) {
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        if (isSignEmpty(sign.getFrontText()) && isSignEmpty(sign.getBackText())) {
            return true; // empty sign, nothing to do
        }
        if (cachedBufferRenderer == null || currentLight != light || (bl && (sign.getFrontText() != front))
                || (!bl && (sign.getBackText() != back))) {
            if (cachedBufferRenderer == null) {
                cachedBufferRenderer = new SignBufferRendererTest(sign, light);
            }
            cachedBufferRenderer.refreshImage(sign, light, bl);
            currentLight = light;
        }
        cachedBufferRenderer.render(poseStack, light, ((Object) this) instanceof HangingSignBlockEntity, bl);
        return true;
    }

    private boolean isSignEmpty(SignText text) {
        for (int i = 0; i < 4; i++) {
            Component line = text.getMessage(i, false);
            if (!line.getString().isBlank())
                return false;
        }
        return true;
    }
}

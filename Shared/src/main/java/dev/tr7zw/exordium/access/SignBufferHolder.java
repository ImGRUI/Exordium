package dev.tr7zw.exordium.access;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;

public interface SignBufferHolder {

    boolean exordium$renderBuffered(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean bl, int light);

}

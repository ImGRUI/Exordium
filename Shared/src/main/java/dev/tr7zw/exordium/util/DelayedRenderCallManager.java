package dev.tr7zw.exordium.util;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Iris causes issues when trying to switch render buffers during world
 * rendering. This class delays the draws to after the world rendering(causes a
 * 1 frame delay in signs, which isn't that bad).
 * 
 * @author tr7zw
 *
 */
public class DelayedRenderCallManager {

    private List<Runnable> renderCalls = new ArrayList<>();
    private List<Runnable> nametagRenderCalls = new ArrayList<>();
    private List<BufferedComponent> componentRenderCalls = new ArrayList<>();
    private Matrix4f usedProjectionMatrix = new Matrix4f();

    public void setProjectionMatrix(Matrix4f mat) {
        this.usedProjectionMatrix = mat;
    }

    public void addRenderCall(Runnable run) {
        renderCalls.add(run);
    }

    public void addNametagRenderCall(Runnable run) {
        nametagRenderCalls.add(run);
    }

    public void addBufferedComponent(BufferedComponent component) {
        this.componentRenderCalls.add(component);
    }

    public void execRenderCalls() {
        for (Runnable run : renderCalls) {
            run.run();
        }
        renderCalls.clear();
        if (!nametagRenderCalls.isEmpty()) {
            Matrix4f backupProjectionMatrix = RenderSystem.getProjectionMatrix();
            NametagScreenBuffer buffer = ExordiumModBase.instance.getNameTagScreenBuffer();
            buffer.bind();
            RenderSystem.setProjectionMatrix(usedProjectionMatrix, RenderSystem.getVertexSorting());
            for (Runnable run : nametagRenderCalls) {
                run.run();
            }
            buffer.bindEnd();
            nametagRenderCalls.clear();
            RenderSystem.setProjectionMatrix(backupProjectionMatrix, RenderSystem.getVertexSorting());
        }
    }

    public void renderComponents() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Model model = BufferedComponent.getModel();
        for (BufferedComponent component : componentRenderCalls) {
            RenderSystem.setShaderTexture(0, component.getTextureId());
            model.draw(RenderSystem.getModelViewMatrix());
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        componentRenderCalls.clear();
    }

}

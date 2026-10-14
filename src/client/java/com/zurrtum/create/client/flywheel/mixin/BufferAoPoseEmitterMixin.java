package com.zurrtum.create.client.flywheel.mixin;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.client.flywheel.lib.model.baked.BufferAoPoseEmitter;
import com.zurrtum.create.client.flywheel.lib.model.baked.FabricEmitterSupplier;
import net.fabricmc.fabric.api.client.renderer.v1.Renderer;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferAoPoseEmitter.class)
public interface BufferAoPoseEmitterMixin extends FabricEmitterSupplier {
    @Shadow(remap = false)
    @Nullable VertexConsumer getBuffer(ChunkSectionLayer layer, boolean shade, boolean ambientOcclusion);

    @Shadow(remap = false)
    Pose getPose();

    @Override
    default @NonNull QuadEmitter quadEmitter() {
        Pose pose = getPose();
        return Renderer.get().quadEmitter(quad -> {
            // 26.3: diffuseShade() was replaced by emissive(); a shaded quad is a non-emissive one.
            VertexConsumer buffer = getBuffer(
                quad.chunkLayer(),
                !quad.emissive(),
                quad.ambientOcclusion().get()
            );
            if (buffer == null) {
                return;
            }
            quad.buffer(OverlayTexture.NO_OVERLAY, pose, buffer);
        });
    }
}

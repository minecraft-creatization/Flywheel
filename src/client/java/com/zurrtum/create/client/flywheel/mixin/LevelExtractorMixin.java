package com.zurrtum.create.client.flywheel.mixin;

import com.zurrtum.create.client.flywheel.impl.FlwImplXplat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dispatches Flywheel's "the level renderer reloaded" event, which is what initialises the library's
 * per-level render machinery: {@code BackendManagerImpl.onReloadLevelRenderer},
 * {@code Uniforms.onReloadLevelRenderer} and {@code ModelRenderHelper.onReloadLevelRenderer} (through
 * {@link FlwImplXplat#dispatchReloadLevelRendererEvent}).
 * <p>
 * <b>Why Flywheel dispatches it itself.</b> The only caller was Create's {@code LevelExtractorMixin}, so
 * on a client without Create nothing ever initialised those, leaving
 * {@code ModelRenderHelper.CULL_INSTANCE} null. The first Ponder scene opened in the standalone
 * ponder-everywhere instance then died with
 * <i>"NullPointerException: Cannot invoke ModelConsumer.updateOutput(BufferEmitterOutput) because
 * ModelRenderHelper.CULL_INSTANCE is null"</i> from {@code SuperByteBufferCache.get} inside
 * {@code WorldSectionElementImpl.renderFirst} - the third hidden Create dependency this standalone run
 * found, and this one is Flywheel's own initialisation, not a consumer's business.
 * <p>
 * Create's mixin keeps the catnip layer caches it clears next to the dispatch; the remaining
 * <em>visualisation</em> hooks of Flywheel's client integration (render context holders, skipping
 * vanilla entity/block-entity draws) still live in Create's mixins, because when Flywheel's own
 * {@code LevelRenderer}/{@code GameRenderer} mixins were split out they were left behind. That is a
 * known, deliberate residual of this pass, not something this hook depends on.
 */
@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {

    @Shadow
    private @Nullable ClientLevel level;

    @Inject(method = "allChanged()V", at = @At("RETURN"))
    private void flywheel$dispatchReloadLevelRenderer(final CallbackInfo ci) {
        if (this.level != null) {
            FlwImplXplat.INSTANCE.dispatchReloadLevelRendererEvent(this.level);
        }
    }
}

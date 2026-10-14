package com.zurrtum.create.client.flywheel.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Flywheel's mixin gate. Carried over from Create's MixinPlugin, which decided these entries by the
 * same condition: the model-emitter mixins only work against Fabric's renderer API, so they are
 * listed in the config but only returned when that module is present.
 */
public class FlwMixinPlugin implements IMixinConfigPlugin {
    private List<String> mixins;

    @Override
    public void onLoad(String mixinPackage) {
        mixins = new ArrayList<>();
        if (FabricLoader.getInstance().isModLoaded("fabric-renderer-api-v1")) {
            mixins.add("BufferAoPoseEmitterMixin");
            mixins.add("BufferColorPoseEmitterMixin");
            mixins.add("BufferEmitterMixin");
            mixins.add("BufferPoseEmitterMixin");
            mixins.add("ItemModelRenderHelperMixin");
            mixins.add("ModelRenderHelperMixin");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

package com.zurrtum.create.client.flywheel.backend.compile;

import com.zurrtum.create.client.flywheel.backend.glsl.ShaderSources;
import com.zurrtum.create.client.flywheel.backend.glsl.SourceComponent;
import com.zurrtum.create.client.flywheel.lib.util.ResourceUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zurrtum.create.client.flywheel.impl.Flywheel.MOD_ID;

public final class FlwPrograms {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + "/backend/shaders");

    private static final Identifier COMPONENTS_HEADER_FRAG = ResourceUtil.rl("internal/components_header.frag");

    public static ShaderSources SOURCES;

    private FlwPrograms() {
    }

    /**
     * Loads (or reloads) Flywheel's programs if they are not ready yet. The reload listener that
     * normally does this is not always invoked - usually because it was registered after the reload
     * that mattered - and without the programs every backend reports itself unsupported.
     */
    public static void ensureLoaded(ResourceManager resourceManager) {
        if (resourceManager == null) {
            return;
        }
        if (InstancingPrograms.allLoaded() && IndirectPrograms.allLoaded()) {
            return;
        }
        try {
            reload(resourceManager);
        } catch (Throwable t) {
            LOGGER.error("Unable to load Flywheel programs", t);
        }
    }

    static void reload(ResourceManager resourceManager) {
        // Reset the programs in case the ubershader load fails.
        InstancingPrograms.setInstance(null);
        IndirectPrograms.setInstance(null);

        var sources = new ShaderSources(resourceManager);
        SOURCES = sources;

        var fragmentComponentsHeader = sources.get(COMPONENTS_HEADER_FRAG);

        List<SourceComponent> vertexComponents = List.of();
        List<SourceComponent> fragmentComponents = List.of(fragmentComponentsHeader);

        InstancingPrograms.reload(sources, vertexComponents, fragmentComponents);
        IndirectPrograms.reload(sources, vertexComponents, fragmentComponents);
    }
}

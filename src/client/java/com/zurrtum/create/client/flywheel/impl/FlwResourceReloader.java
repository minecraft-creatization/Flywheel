package com.zurrtum.create.client.flywheel.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import static com.zurrtum.create.client.flywheel.impl.Flywheel.MOD_ID;

/**
 * Flywheel's own resource-reload listener base. Previously this extended Create's
 * CreateResourceReloader; it is inlined here so the Flywheel module does not depend on Create
 * (or on the Ponder module that owns catnip).
 */
public abstract class FlwResourceReloader implements ResourceManagerReloadListener {
    private final Identifier id;

    public FlwResourceReloader(String id) {
        this.id = Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    public FlwResourceReloader(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }
}

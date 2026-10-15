package com.zurrtum.create.client.flywheel.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Mounts Flywheel's own jar resources as a built-in client resource pack.
 *
 * <p>Flywheel used to live inside Create's jar, and Create's {@code ClientPackSourceMixin} exposes
 * the {@code flywheel} namespace from its own root paths, which mounted these assets for free. After
 * the split the assets sit in Flywheel's jar, so Flywheel has to mount them itself: without this
 * mixin {@code ShaderSources} ({@code manager.listResources("flywheel", ...)}) finds no {@code .glsl}
 * sources under {@code assets/flywheel/flywheel/}, the ubershader load fails and every backend
 * reports itself unsupported -- Create then silently falls back to vanilla rendering.
 *
 * <p>Only Flywheel's own namespace is exposed; Create's pack still exposes {@code minecraft},
 * {@code vanillin}, {@code ponder} and {@code fabric} from Create's jar, and Ponder mounts
 * {@code ponder} from its own. The pack id, the {@link KnownPack} version string and the
 * {@code BOTTOM} position are deliberately identical to Create's arrangement so pack ordering does
 * not change.
 *
 * <p>Gated on Fabric API being absent: when it is present, its {@code fabric-resource-loader-v0/v1}
 * module registers a built-in pack for every mod, and a second registration would duplicate the pack
 * in the list. This mirrors Create's own gate rather than inventing a second policy.
 */
@Mixin(ClientPackSource.class)
public abstract class ClientPackSourceMixin {
    @Inject(method = "populatePackList(Ljava/util/function/BiConsumer;)V", at = @At("TAIL"))
    private void flywheel$addBuiltinPack(BiConsumer<String, Function<String, Pack>> discoveredPacks, CallbackInfo ci) {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("fabric-api")) {
            return;
        }
        ModContainer mod = loader.getModContainer("flywheel").orElseThrow();
        List<Path> rootPaths = mod.getRootPaths();
        ModMetadata metadata = mod.getMetadata();
        discoveredPacks.accept(
            "flywheel", id -> {
                String directory = PackType.CLIENT_RESOURCES.getDirectory();
                PackLocationInfo info = createInfo(id, metadata.getName(), directory, metadata.getVersion().getFriendlyString());
                ResourceMetadata meta = createMeta(Component.literal("Flywheel built-in resources"));
                PackSelectionConfig position = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
                return createPacket(rootPaths, directory, info, meta, position, "flywheel");
            }
        );
    }

    @Unique
    private static PackLocationInfo createInfo(String id, String name, String path, String version) {
        return new PackLocationInfo(
            id,
            Component.literal(name),
            PackSource.BUILT_IN,
            Optional.of(new KnownPack(id, path, version))
        );
    }

    @Unique
    private static ResourceMetadata createMeta(Component description) {
        return ResourceMetadata.of(
            PackMetadataSection.CLIENT_TYPE,
            new PackMetadataSection(
                description,
                SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange()
            )
        );
    }

    @Unique
    private static Pack createPacket(
        List<Path> rootPaths,
        String directory,
        PackLocationInfo info,
        ResourceMetadata meta,
        PackSelectionConfig position,
        String... namespace
    ) {
        VanillaPackResourcesBuilder builder = new VanillaPackResourcesBuilder().setMetadata(meta)
            .exposeNamespace(namespace);
        for (Path path : rootPaths) {
            builder.pushAssetPath(PackType.CLIENT_RESOURCES, path.resolve(directory));
        }
        VanillaPackResources pack = builder.build(info);
        return Pack.readMetaAndCreate(info, pack.asResourcesSupplier(), PackType.CLIENT_RESOURCES, position);
    }
}

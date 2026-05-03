package com.liquidhydrogen.mcfork.worldgen.density;

import com.liquidhydrogen.mcfork.McForkMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registry hook for mc-fork's custom density-function types.
 *
 * <p>Mod entry point ({@link McForkMod}) calls {@link #register(IEventBus)} once during
 * mod construction. NeoForge's deferred-register machinery delays the actual put-into-registry
 * until the mod-event-bus REGISTER event fires, so this class can be referenced safely from
 * the @Mod constructor without risk of touching a not-yet-initialised registry.
 */
public final class ModDensityFunctions {

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES =
            DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE, McForkMod.MOD_ID);

    @SuppressWarnings("unused") // Held to keep the registration alive and discoverable in IDE listings.
    public static final Supplier<MapCodec<ZClampedGradient>> Z_CLAMPED_GRADIENT =
            DENSITY_FUNCTION_TYPES.register("z_clamped_gradient", () -> ZClampedGradient.codecInstance().codec());

    private ModDensityFunctions() {
        throw new AssertionError("registry holder, do not instantiate");
    }

    public static void register(IEventBus modEventBus) {
        DENSITY_FUNCTION_TYPES.register(modEventBus);
    }
}

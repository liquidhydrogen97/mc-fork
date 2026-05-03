package com.liquidhydrogen.mcfork.worldgen.density;

import net.minecraft.util.Mth;

/**
 * Pure-math kernel for clamped-gradient density functions. Lives outside the
 * {@link net.minecraft.world.level.levelgen.DensityFunction} adapter so it can be unit-tested
 * without loading the {@code DensityFunction}/{@code DensityFunctions} class hierarchy --
 * those classes' static initializers cascade into {@code BuiltInRegistries} which require
 * a Minecraft bootstrap that is impractical to stand up in JVM unit tests on a NeoForge
 * test classpath (FeatureFlagLoader needs a live FML LoadingModList).
 *
 * <p>Both {@link ZClampedGradient#computeAtZ(int)} and (eventually) any future axis-clamped
 * variants delegate here. The math is a one-liner around {@link Mth#clampedMap}; this class
 * exists strictly as a test seam.
 */
final class ClampedGradientMath {

    private ClampedGradientMath() {
        throw new AssertionError("math kernel, do not instantiate");
    }

    /**
     * Linearly interpolate between {@code fromValue} at {@code fromCoord} and {@code toValue}
     * at {@code toCoord}, clamping outside the band. Mirrors vanilla's {@code Mth.clampedMap}
     * semantics: when {@code fromCoord > toCoord} the gradient is reversed but the clamp at
     * each anchor still returns the corresponding value.
     */
    static double clampedMap(int coord, int fromCoord, int toCoord, double fromValue, double toValue) {
        return Mth.clampedMap((double) coord, (double) fromCoord, (double) toCoord, fromValue, toValue);
    }

    /**
     * The minimum output of a clamped-gradient interpolation between {@code fromValue} and
     * {@code toValue}. Independent of the coordinate band.
     */
    static double minValue(double fromValue, double toValue) {
        return Math.min(fromValue, toValue);
    }

    /**
     * The maximum output of a clamped-gradient interpolation between {@code fromValue} and
     * {@code toValue}. Independent of the coordinate band.
     */
    static double maxValue(double fromValue, double toValue) {
        return Math.max(fromValue, toValue);
    }
}

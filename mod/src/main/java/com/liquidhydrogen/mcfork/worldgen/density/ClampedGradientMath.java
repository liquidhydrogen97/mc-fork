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

    /**
     * Z-axis anchor bound for the {@code z_clamped_gradient} codec. Mirrors
     * {@code WorldBorder.MAX_CENTER_COORDINATE} (29_999_984.0, rounded to a clean
     * 30_000_000). Z is bounded by the world border, not by build height -- the analogous
     * vanilla {@code YClampedGradient} bounds its anchors by {@code DimensionType.MIN_Y/MAX_Y * 2}
     * (~+/-4064), which is correct for build height but structurally wrong for Z.
     *
     * <p>Lives here rather than on {@link ZClampedGradient} so unit tests can read the
     * constant without triggering {@code ZClampedGradient.<clinit>}, which cascades through
     * {@code DensityFunction} -> {@code DensityFunctions} -> {@code BuiltInRegistries} and
     * requires a Minecraft bootstrap that JUnit cannot reasonably stand up under NeoForge.
     */
    static final int Z_ANCHOR_MIN = -30_000_000;
    static final int Z_ANCHOR_MAX = 30_000_000;

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

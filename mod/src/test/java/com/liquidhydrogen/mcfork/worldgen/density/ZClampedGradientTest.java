package com.liquidhydrogen.mcfork.worldgen.density;

import com.liquidhydrogen.mcfork.McForkMod;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the math kernel that backs {@link ZClampedGradient}. The tests target
 * {@link ClampedGradientMath} rather than the {@code ZClampedGradient} record itself because
 * loading {@code ZClampedGradient} cascades into {@code DensityFunction} -> {@code DensityFunctions}
 * static init, which on a NeoForge runtime classpath transitively requires
 * {@code BuiltInRegistries} to be bootstrapped. Standing up that bootstrap inside JUnit is
 * impractical here (NeoForge's FeatureFlagLoader expects a live FML LoadingModList), so we
 * extract the math to a non-DensityFunction kernel and verify the codec / dispatch / FunctionContext
 * wiring at the integration tier via runClient + datapack load (D1.2 AC#3 + AC#4).
 *
 * <p>Test plan in contracts/D1.2.md: boundary returns, midpoint, quarter-point, two-sided
 * clamp, inverted range, negative range, min/max envelope, registration key shape.
 *
 * <p>Tolerance: integer fromZ/toZ make most cases exact; {@code 1e-9} absorbs floating-point
 * drift on non-power-of-two midpoints.
 */
class ZClampedGradientTest {

    private static final double EPS = 1e-9;

    @Test
    void fromValueAtMinZ() {
        assertEquals(1.0, ClampedGradientMath.clampedMap(0, 0, 300_000, 1.0, -1.0), EPS);
    }

    @Test
    void toValueAtMaxZ() {
        assertEquals(-1.0, ClampedGradientMath.clampedMap(300_000, 0, 300_000, 1.0, -1.0), EPS);
    }

    @Test
    void midpoint() {
        assertEquals(0.0, ClampedGradientMath.clampedMap(150_000, 0, 300_000, 1.0, -1.0), EPS);
    }

    @Test
    void quarterPoint() {
        // 0 + 0.25 * (1.0 - 0.0) = 0.25 at z = 100_000
        assertEquals(0.25, ClampedGradientMath.clampedMap(100_000, 0, 400_000, 0.0, 1.0), EPS);
    }

    @Test
    void clampBelowMin() {
        assertEquals(1.0, ClampedGradientMath.clampedMap(-1_000_000, 0, 300_000, 1.0, -1.0), EPS);
    }

    @Test
    void clampAboveMax() {
        assertEquals(-1.0, ClampedGradientMath.clampedMap(1_000_000, 0, 300_000, 1.0, -1.0), EPS);
    }

    @Test
    void invertedRange() {
        // fromZ > toZ. Mth.clampedMap reproduces a monotonic linear interpolation between the two
        // anchors regardless of which is larger; below the min anchor it returns the corresponding
        // value, above the max anchor likewise. With fromZ=300_000 > toZ=0:
        //   z=0       -> toValue=1.0     (z is at the toZ anchor)
        //   z=300_000 -> fromValue=-1.0  (z is at the fromZ anchor)
        //   z=150_000 -> midpoint=0.0    (linear between)
        assertAll(
                () -> assertEquals(-1.0, ClampedGradientMath.clampedMap(300_000, 300_000, 0, -1.0, 1.0), EPS),
                () -> assertEquals(1.0, ClampedGradientMath.clampedMap(0, 300_000, 0, -1.0, 1.0), EPS),
                () -> assertEquals(0.0, ClampedGradientMath.clampedMap(150_000, 300_000, 0, -1.0, 1.0), EPS)
        );
    }

    @Test
    void negativeZRange() {
        assertAll(
                () -> assertEquals(-0.7, ClampedGradientMath.clampedMap(-300_000, -300_000, 0, -0.7, 0.0), EPS),
                () -> assertEquals(0.0, ClampedGradientMath.clampedMap(0, -300_000, 0, -0.7, 0.0), EPS),
                () -> assertEquals(-0.35, ClampedGradientMath.clampedMap(-150_000, -300_000, 0, -0.7, 0.0), EPS),
                // Beyond min anchor clamps
                () -> assertEquals(-0.7, ClampedGradientMath.clampedMap(-1_000_000, -300_000, 0, -0.7, 0.0), EPS)
        );
    }

    @Test
    void minMaxValueAscending() {
        assertAll(
                () -> assertEquals(-1.0, ClampedGradientMath.minValue(1.0, -1.0), EPS),
                () -> assertEquals(1.0, ClampedGradientMath.maxValue(1.0, -1.0), EPS)
        );
    }

    @Test
    void minMaxValueDescending() {
        assertAll(
                () -> assertEquals(-0.7, ClampedGradientMath.minValue(-0.7, 0.0), EPS),
                () -> assertEquals(0.0, ClampedGradientMath.maxValue(-0.7, 0.0), EPS)
        );
    }

    @Test
    void registrationKey() {
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(McForkMod.MOD_ID, "z_clamped_gradient");
        assertEquals("mc_fork", key.getNamespace());
        assertEquals("z_clamped_gradient", key.getPath());
    }

    @Test
    void mathKernelIsNonInstantiable() throws Exception {
        // Cover the static-utility constructor path: it must throw to prevent reflective
        // misuse, which both documents intent and lets JaCoCo see the line.
        java.lang.reflect.Constructor<ClampedGradientMath> ctor =
                ClampedGradientMath.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        java.lang.reflect.InvocationTargetException ex =
                org.junit.jupiter.api.Assertions.assertThrows(
                        java.lang.reflect.InvocationTargetException.class,
                        ctor::newInstance);
        org.junit.jupiter.api.Assertions.assertTrue(ex.getCause() instanceof AssertionError);
    }

    /**
     * Regression: the codec must accept Z anchors as large as Phase 1 needs ({@code +/-300_000})
     * and reject anchors outside the world border ({@code +/-30_000_000}). The original
     * implementation copied the bound from vanilla {@code YClampedGradient}
     * ({@code DimensionType.MIN_Y * 2 .. MAX_Y * 2}, i.e. roughly +/-4064), which was
     * structurally wrong for a Z-axis variant: Z is bounded by the world border, not by
     * build height, so {@code latitude_bias.json} failed to parse with
     * {@code "Value -300000 outside of range [-4064:4062]"}.
     *
     * <p>The actual JSON parse is exercised at the integration tier (runClient + datapack
     * load -- AC#5). Here we pin the bound constants on {@link ClampedGradientMath}, which
     * is the source of truth the codec reads from -- so a future change that drifts the
     * bound back into build-height territory fails this test, not just the playtest.
     */
    @Test
    void codecAnchorBoundsCoverPhase1Range() {
        assertAll(
                "Codec bound must permit Phase 1 latitude_bias anchors and reject coords beyond the world border",
                () -> assertEquals(-30_000_000, ClampedGradientMath.Z_ANCHOR_MIN,
                        "Z_ANCHOR_MIN should match WorldBorder.MAX_CENTER_COORDINATE"),
                () -> assertEquals(30_000_000, ClampedGradientMath.Z_ANCHOR_MAX,
                        "Z_ANCHOR_MAX should match WorldBorder.MAX_CENTER_COORDINATE"),
                () -> org.junit.jupiter.api.Assertions.assertTrue(
                        300_000 >= ClampedGradientMath.Z_ANCHOR_MIN
                                && 300_000 <= ClampedGradientMath.Z_ANCHOR_MAX,
                        "Phase 1 latitude_bias.json uses 300_000 as a from_z/to_z; codec must accept it"),
                () -> org.junit.jupiter.api.Assertions.assertTrue(
                        -300_000 >= ClampedGradientMath.Z_ANCHOR_MIN
                                && -300_000 <= ClampedGradientMath.Z_ANCHOR_MAX,
                        "Phase 1 latitude_bias.json uses -300_000 as a from_z/to_z; codec must accept it"),
                () -> org.junit.jupiter.api.Assertions.assertTrue(
                        31_000_000 > ClampedGradientMath.Z_ANCHOR_MAX,
                        "Anchors beyond the world border (31_000_000) must be outside the codec bound")
        );
    }
}

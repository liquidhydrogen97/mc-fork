package com.liquidhydrogen.mcfork.worldgen.density;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Z-axis analogue of vanilla's {@code minecraft:y_clamped_gradient}. Reads the world Z
 * coordinate and linearly interpolates between {@code fromValue} at {@code fromZ} and
 * {@code toValue} at {@code toZ}, clamping outside the band.
 *
 * <p>Used by Phase 1 to drive the latitude temperature bias: small or zero near z=0,
 * negative (cold) at large {@code |z|}.
 *
 * <p>Mirrors {@code DensityFunctions.YClampedGradient} -- same field types, same
 * {@code Mth.clampedMap} math (delegated to {@link ClampedGradientMath}), same codec
 * shape -- but reads {@link DensityFunction.FunctionContext#blockZ()}.
 *
 * @param fromZ     World Z anchor where output equals {@code fromValue}.
 * @param toZ       World Z anchor where output equals {@code toValue}.
 * @param fromValue Output at {@code fromZ}.
 * @param toValue   Output at {@code toZ}.
 */
public record ZClampedGradient(int fromZ, int toZ, double fromValue, double toValue)
        implements DensityFunction.SimpleFunction {

    /**
     * NoiseRouter density function values are bounded to {@code [-1_000_000, 1_000_000]}
     * by Mojang convention; we use the same bound so JSON deserialization rejects nonsense.
     */
    private static final double VALUE_MIN = -1_000_000.0;
    private static final double VALUE_MAX = 1_000_000.0;

    /**
     * Lazy codec holder. Mirrors the standard initialization-on-demand idiom: the codec is
     * built on first access, not at {@code ZClampedGradient.<clinit>}. This matters because
     * the codec references {@link DimensionType#MIN_Y} / {@link DimensionType#MAX_Y}, whose
     * loading cascades through {@code BuiltInRegistries} and therefore requires that
     * {@code Bootstrap.bootStrap()} has already run. In production FML triggers bootstrap
     * before the registration phase touches this class; in JVM unit tests we want to exercise
     * the math path without booting Minecraft, so deferring codec construction keeps the test
     * classpath honest.
     */
    private static final class CodecHolder {
        static final MapCodec<ZClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                com.mojang.serialization.Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2)
                        .fieldOf("from_z").forGetter(ZClampedGradient::fromZ),
                com.mojang.serialization.Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2)
                        .fieldOf("to_z").forGetter(ZClampedGradient::toZ),
                com.mojang.serialization.Codec.doubleRange(VALUE_MIN, VALUE_MAX)
                        .fieldOf("from_value").forGetter(ZClampedGradient::fromValue),
                com.mojang.serialization.Codec.doubleRange(VALUE_MIN, VALUE_MAX)
                        .fieldOf("to_value").forGetter(ZClampedGradient::toValue)
        ).apply(instance, ZClampedGradient::new));

        static final KeyDispatchDataCodec<ZClampedGradient> CODEC = new KeyDispatchDataCodec<>(DATA_CODEC);
    }

    public static KeyDispatchDataCodec<ZClampedGradient> codecInstance() {
        return CodecHolder.CODEC;
    }

    @Override
    public double compute(FunctionContext context) {
        return ClampedGradientMath.clampedMap(context.blockZ(), fromZ, toZ, fromValue, toValue);
    }

    @Override
    public double minValue() {
        return ClampedGradientMath.minValue(fromValue, toValue);
    }

    @Override
    public double maxValue() {
        return ClampedGradientMath.maxValue(fromValue, toValue);
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CodecHolder.CODEC;
    }
}

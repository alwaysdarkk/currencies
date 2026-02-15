package com.github.alwaysdarkk.currencies.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;

@UtilityClass
public class NumberUtil {

    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.##");
    private final List<Suffix> SUFFIXES = List.of(
            new Suffix("", 1D),
            new Suffix("k", 1_000D),
            new Suffix("M", 1_000_000D),
            new Suffix("B", 1E9),
            new Suffix("T", 1E12),
            new Suffix("Q", 1E15),
            new Suffix("QQ", 1E18),
            new Suffix("S", 1E21),
            new Suffix("SS", 1E24),
            new Suffix("O", 1E27),
            new Suffix("N", 1E30),
            new Suffix("D", 1E33),
            new Suffix("UN", 1E36),
            new Suffix("DD", 1E39),
            new Suffix("TR", 1E42),
            new Suffix("QT", 1E45),
            new Suffix("QN", 1E48),
            new Suffix("SD", 1E51),
            new Suffix("SPD", 1E54),
            new Suffix("OD", 1E57),
            new Suffix("ND", 1E60),
            new Suffix("VG", 1E63),
            new Suffix("UVG", 1E66),
            new Suffix("DVG", 1E69),
            new Suffix("TVG", 1E72),
            new Suffix("QTV", 1E75),
            new Suffix("QNV", 1E78),
            new Suffix("SEV", 1E81),
            new Suffix("SPV", 1E84),
            new Suffix("OVG", 1E87),
            new Suffix("NVG", 1E90),
            new Suffix("ITG", 1E93)
    );
    private final double LOG = Math.log(1000);

    public @Nonnull String formatWithSuffix(double number) {
        if (isInvalid(number)) {
            return "0";
        }

        if (number < 1000) {
            return format(number);
        }

        int index = (int) (Math.log(number) / LOG);
        index = Math.clamp(index, 0, SUFFIXES.size() - 1);

        final Suffix suffix = SUFFIXES.get(index);
        return DECIMAL_FORMAT.format(number / suffix.value()) + suffix.symbol();
    }

    public @Nonnull String format(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    public boolean isInvalid(double number) {
        return number <= 0.0 || Double.isNaN(number) || Double.isInfinite(number);
    }

    private record Suffix(String symbol, double value) {}
}
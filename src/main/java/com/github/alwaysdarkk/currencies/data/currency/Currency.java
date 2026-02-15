package com.github.alwaysdarkk.currencies.data.currency;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Data;

@Data
public class Currency {
    public static final BuilderCodec<Currency> CURRENCY_CODEC = BuilderCodec.builder(Currency.class, Currency::new)
            .append(new KeyedCodec<>("Id", Codec.STRING), Currency::setId, Currency::getId)
            .add()
            .append(new KeyedCodec<>("Name", Codec.STRING), Currency::setName, Currency::getName)
            .add()
            .append(new KeyedCodec<>("Color", Codec.STRING), Currency::setColor, Currency::getColor)
            .add()
            .append(
                    new KeyedCodec<>("DefaultAmount", Codec.DOUBLE),
                    Currency::setDefaultAmount,
                    Currency::getDefaultAmount)
            .add()
            .append(new KeyedCodec<>("IsPayEnable", Codec.BOOLEAN), Currency::setPayEnable, Currency::isPayEnable)
            .add()
            .build();

    private String id;
    private String name;
    private String color;
    private double defaultAmount;
    private boolean payEnable;
}

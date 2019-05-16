package edu.iis.mto.testreactor.exc3;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Banknote {
    PL10(10, Currency.PL),
    PL20(20, Currency.PL),
    PL50(50, Currency.PL),
    PL100(100, Currency.PL),
    PL200(200, Currency.PL),
    PL500(500, Currency.PL),
    EU10(10, Currency.EU),
    EU20(20, Currency.EU),
    EU50(50, Currency.EU),
    EU100(100, Currency.EU),
    EU200(200, Currency.EU),
    EU500(500, Currency.EU);

    private int value;
    private Currency currency;

    private Banknote(int value, Currency currency) {
        this.value = value;
        this.currency = Objects.requireNonNull(currency, "currency == null");
    }

    public int getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static List<Banknote> forCurrency(Currency currency) {
        return Stream.of(values())
                     .filter(banknote -> banknote.currency.equals(currency))
                     .collect(Collectors.toList());
    }
}

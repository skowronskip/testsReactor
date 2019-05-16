package edu.iis.mto.testreactor.exc3;

import java.util.Objects;

public class Money {

    private final int amount;
    private final Currency currency;

    private Money(Builder builder) {
        this.amount = builder.amount;
        this.currency = Objects.requireNonNull(builder.currency, "currency == null");
    }

    public int getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Money other = (Money) obj;
        return amount == other.amount && currency == other.currency;
    }

    @Override
    public String toString() {
        return "Money [amount=" + amount + ", currency=" + currency + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int amount;
        private Currency currency;

        private Builder() {}

        public Builder withAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Money build() {
            return new Money(this);
        }
    }

}

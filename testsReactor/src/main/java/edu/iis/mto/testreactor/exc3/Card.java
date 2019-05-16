package edu.iis.mto.testreactor.exc3;

public class Card {

    private final String cardNumber;
    private final int pinNumber;

    private Card(Builder builder) {
        this.cardNumber = builder.cardNumber;
        this.pinNumber = builder.pinNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    @Override
    public String toString() {
        return "Card [cardNumber=" + cardNumber + ", pinNumber=" + pinNumber + "]";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String cardNumber;
        private int pinNumber;

        private Builder() {}

        public Builder withCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder withPinNumber(int pinNumber) {
            this.pinNumber = pinNumber;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }
}

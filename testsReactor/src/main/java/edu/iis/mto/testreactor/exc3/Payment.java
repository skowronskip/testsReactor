package edu.iis.mto.testreactor.exc3;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

public class Payment {

    private final List<Banknote> value;

    public Payment(List<Banknote> value) {
        this.value = Objects.requireNonNull(value, "value == null")
                            .stream()
                            .sorted()
                            .collect(toList());
    }

    public List<Banknote> getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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
        Payment other = (Payment) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return "Withdrawal [value=" + value + "]";
    }

}

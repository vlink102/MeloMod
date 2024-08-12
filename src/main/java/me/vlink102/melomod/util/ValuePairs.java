package me.vlink102.melomod.util;

import lombok.Getter;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Getter
public class ValuePairs<F, S> {
    private final F first;
    private final S second;

    public ValuePairs(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public <T> T product(BiFunction<F, S, T> function) {
        return function.apply(this.first, this.second);
    }

    public void consume(BiConsumer<F, S> consumer) {
        consumer.accept(this.first, this.second);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ValuePairs<?, ?> that = (ValuePairs)o;
            return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.first, this.second});
    }
}

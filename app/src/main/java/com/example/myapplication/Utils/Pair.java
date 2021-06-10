package com.example.myapplication.Utils;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;

import java.io.Serializable;

public class Pair<F, S> implements Serializable {
    public final F first;
    public final S second;

    /**
     * Constructor for a Pair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    @SuppressWarnings("UnknownNullness") // Generic nullness should come from type annotations.
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link androidx.core.util.Pair} to which this one is to be checked for equality
     * @return true if the underlying objects of the Pair are both considered
     *         equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof androidx.core.util.Pair)) {
            return false;
        }
        androidx.core.util.Pair<?, ?> p = (androidx.core.util.Pair<?, ?>) o;
        return ObjectsCompat.equals(p.first, first) && ObjectsCompat.equals(p.second, second);
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }

    @NonNull
    @Override
    public String toString() {
        return "Pair{" + first + " " + second + "}";
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    @NonNull
    @SuppressWarnings("UnknownNullness") // Generic nullness should come from type annotations.
    public static <A, B> androidx.core.util.Pair<A, B> create(A a, B b) {
        return new androidx.core.util.Pair<>(a, b);
    }
}

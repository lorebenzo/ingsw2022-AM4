package it.polimi.ingsw.utils.multilist;

public interface MultiListPredicate<T, U, V> {
    boolean check(T first, U second, V third);
}

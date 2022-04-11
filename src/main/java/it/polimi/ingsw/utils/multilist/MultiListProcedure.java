package it.polimi.ingsw.utils.multilist;

public interface MultiListProcedure<T, U, V> {
    void apply(T first, U second, V third);
}

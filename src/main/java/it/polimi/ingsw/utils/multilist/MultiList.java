package it.polimi.ingsw.utils.multilist;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Implements a list whose elements are tuples of 3 elements
 * @param <T> first element type
 * @param <U> second element type
 * @param <V> third element type
 */
public class MultiList<T extends Comparable<T>, U, V> {
    private final List<T> first = new LinkedList<>();
    private final List<U> second = new LinkedList<>();
    private final List<V> third = new LinkedList<>();

    public MultiList() {
    }

    /**
     *
     * @param index index of the element to return
     * @return the first element of the tuple at the specified position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    public T getFirst(int index) {
        return this.first.get(index);
    }

    /**
     *
     * @param index index of the element to return
     * @return the second element of the tuple at the specified position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    public U getSecond(int index) {
        return this.second.get(index);
    }

    /**
     *
     * @param index index of the element to return
     * @return the third element of the tuple at the specified position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    public V getThird(int index) {
        return this.third.get(index);
    }

    /**
     * Adds the tuple to the multi-list
     * @param first first element of the tuple
     * @param second second element of the tuple
     * @param third third element of the tuple
     */
    public void add(T first, U second, V third) {
        this.first.add(first);
        this.second.add(second);
        this.third.add(third);
    }

    /**
     * Removes the first occurrence of a tuple whose first element is equal to the specified one
     * @param firstKey the first element of the tuple to remove
     */
    public void remove(T firstKey) {
        Optional<T> objectFound = this.first.stream().filter(x -> x.compareTo(firstKey) == 0)
                .findFirst();

        if(objectFound.isPresent()) {
            var index = this.first.indexOf(objectFound.get());
            if(index != -1) {
                this.first.remove(index);
                this.second.remove(index);
                this.third.remove(index);
            }
        }
    }

    /**
     *
     * @return the number of tuples in the multi-list
     */
    public int size() {
        return this.first.size();
    }

    /**
     *
     * @param predicate a predicate that takes a tuple as input
     * @return a multi-list containing the elements of this list that make the predicate true
     */
    public MultiList<T, U, V> filter(MultiListPredicate<T, U, V> predicate) {
        MultiList<T, U, V> newMultiList = new MultiList<>();
        for(int i = 0; i < this.size(); i++) {
            if(predicate.check(
                    this.first.get(i),
                    this.second.get(i),
                    this.third.get(i)
            )) newMultiList.add(this.first.get(i), this.second.get(i), this.third.get(i));
        }
        return newMultiList;
    }

    /**
     * Applies the procedure to each tuple in the multi-list
     * @param procedure a procedure that takes a tuple as input
     */
    public void forEach(MultiListProcedure<T, U, V> procedure) {
        for(int i = 0; i < this.size(); i++) {
            procedure.apply(
                    this.first.get(i),
                    this.second.get(i),
                    this.third.get(i)
            );
        }
    }
}


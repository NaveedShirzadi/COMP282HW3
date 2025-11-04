import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Objects;

/**
 *  This class implements some of the methods of the Java
 *  ArrayList class.
 * @param <E> The element type
 *  @author .
 */
public class CSArrayList<E> extends AbstractList<E> {

    /** The default initial capacity */
    private static final int INITIAL_CAPACITY = 10;

    /** The underlying data array */
    private E[] theData;

    /** The current size */
    private int size = 0;

    /** The current capacity */
    private int capacity = 0;

    /**
     * Construct an empty CSArrayList with the default initial capacity
     */
    @SuppressWarnings("unchecked")
    public CSArrayList() {
        capacity = INITIAL_CAPACITY;
        theData = (E[]) new Object[capacity];
    }

    /**
     * Construct an empty CSArrayList with a specified initial capacity
     * @param capacity The initial capacity
     */
    @SuppressWarnings("unchecked")
    public CSArrayList(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        this.capacity = Math.max(1, capacity);
        theData = (E[]) new Object[this.capacity];
    }

    /**
     * Construct an ArrayList<E> from any Collection whose elements are E or a subtype of E.
     * @param c The Collection
     */
    @SuppressWarnings("unchecked")
    public CSArrayList(Collection<? extends E> c) {
        capacity = Math.max(INITIAL_CAPACITY, c.size());
        theData = (E[]) new Object[capacity];
        for (E e : c) {
            add(e);
        }
    }

    /**
     * Add an entry to the data inserting it at the end of the list.
     * @param anEntry The value to be added to the list.
     * @return true since the add always succeeds
     */
    @Override
    public boolean add(E anEntry) {
        if (size == capacity) {
            reallocate();
        }
        theData[size] = anEntry;
        size++;
        modCount++;
        return true;
    }

    /**
     * Add an entry to the data inserting it at index of the list.
     * @param index - The index of the item desired
     * @param anEntry The value to be added to the list.
     */
    @Override
    public void add(int index, E anEntry) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        if (size == capacity) {
            reallocate();
        }
        for (int i = size; i > index; i--) {
            theData[i] = theData[i - 1];
        }
        theData[index] = anEntry;
        size++;
        modCount++;
    }


    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        return theData[index];
    }


    @Override
    public E set(int index, E newValue) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        E oldValue = theData[index];
        theData[index] = newValue;
        return oldValue;
    }


    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        E returnValue = theData[index];
        for (int i = index + 1; i < size; i++) {
            theData[i - 1] = theData[i];
        }
        size--;
        theData[size] = null;  // clear freed slot to avoid memory retention
        modCount++;            // fail-fast iterator support
        return returnValue;
    }


    private void reallocate() {
        capacity = (capacity == 0) ? INITIAL_CAPACITY : 2 * capacity;
        theData = Arrays.copyOf(theData, capacity);
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public int indexOf(Object item) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(theData[i], item)) return i;
        }
        return -1;
    }


    @Override // formats the last and if the list is empty it returns []
    public String toString() {
        if (size == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(theData[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }


    @Override // goes through the loop changing elements to zero while also increment modcount to show structure has changed
    public void clear() {
        for (int i = 0; i < size; i++) {
            theData[i] = null;
        }
        size = 0;
        modCount++;
    }


    @Override // checks to see if the array is empty or not
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean remove(Object o) {
        int idx = indexOf(o);
        if (idx == -1) return false;
        for (int i = idx + 1; i < size; i++) {
            theData[i - 1] = theData[i];
        }
        theData[--size] = null;
        modCount++;
        return true;
    }

    /**
     * Ensures that the list has at least the specified capacity.
     * @param minCapacity the minimum capacity needed
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity <= capacity) return;
        int newCap = Math.max(minCapacity, Math.max(1, capacity * 2));
        theData = Arrays.copyOf(theData, newCap);
        capacity = newCap;
    }

    /**
     * Shrinks the backing array to match the current size of the list.
     * Saves memory if the list has a lot of unused space.
     */
    public void trimToSize() {
        if (capacity == size) return;
        theData = Arrays.copyOf(theData, size);
        capacity = size;
    }
}

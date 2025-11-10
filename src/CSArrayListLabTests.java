import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers: constructors, edge indices, resizing, nulls/duplicates,
 * remove(Object), fail-fast iterator, ensureCapacity/trimToSize,
 * clear/isEmpty, toString formatting, and indexOf behavior.
 */
public class CSArrayListLabTests {

    @Test
    @DisplayName("Default constructor: empty list with size=0")
    void defaultCtor_startsEmpty() {
        CSArrayList<Integer> list = new CSArrayList<>();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertEquals("[]", list.toString());
    }

    @Test
    @DisplayName("Capacity constructor: initializes and accepts adds")
    void capacityCtor_acceptsAdds() {
        CSArrayList<String> list = new CSArrayList<>(32);
        assertEquals(0, list.size());
        list.add("A");
        list.add("B");
        assertEquals(2, list.size());
        assertEquals("[A, B]", list.toString());
    }

    @Test
    @DisplayName("Capacity constructor: throws on negative capacity")
    void capacityCtor_negativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CSArrayList<String>(-1));
    }

    @Test
    @DisplayName("Collection constructor: copies elements in order")
    void collectionCtor_copiesElements() {
        Collection<String> src = Arrays.asList("A", "B", "C");
        CSArrayList<String> list = new CSArrayList<>(src);
        assertEquals(3, list.size());
        assertEquals("[A, B, C]", list.toString());
        assertEquals(0, list.indexOf("A"));
        assertEquals(2, list.indexOf("C"));
    }


    @Test
    @DisplayName("add(int,index) at head, middle, and tail")
    void addAtEdges_headMiddleTail() {
        CSArrayList<Integer> list = new CSArrayList<>();
        list.add(0, 1); // head
        list.add(1, 3); // tail
        list.add(1, 2); // middle
        assertEquals("[1, 2, 3]", list.toString());
        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("add(int,index): bounds checking")
    void addAtIndex_bounds() {
        CSArrayList<Integer> list = new CSArrayList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 99)); // size==0, valid indices: only 0
    }

    @Test
    @DisplayName("get/set/remove: bounds and correctness")
    void getSetRemove_boundsAndValues() {
        CSArrayList<String> list = new CSArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        // get bounds
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));

        // set returns old value and updates element
        String old = list.set(1, "X");
        assertEquals("B", old);
        assertEquals("[A, X, C]", list.toString());

        // remove returns removed element and shifts
        String removed = list.remove(0); // remove A
        assertEquals("A", removed);
        assertEquals("[X, C]", list.toString());

        // remove bounds
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2)); // size==2, valid: 0..1
    }


    @Test
    @DisplayName("Appending many items resizes correctly and preserves order")
    void manyAppends_resizeAndOrder() {
        CSArrayList<Integer> list = new CSArrayList<>();
        int N = 20_000;
        for (int i = 0; i < N; i++) list.add(i);
        assertEquals(N, list.size());
        // spot-check a few values
        assertEquals(0, list.get(0));
        assertEquals(1234, list.get(1234));
        assertEquals(N - 1, list.get(N - 1));
    }

    @Test
    @DisplayName("ensureCapacity increases capacity without changing size/content")
    void ensureCapacity_doesNotChangeSize() {
        CSArrayList<String> list = new CSArrayList<>();
        list.add("A");
        list.add("B");
        int sizeBefore = list.size();

        list.ensureCapacity(1000); // should not change size or content
        assertEquals(sizeBefore, list.size());
        assertEquals("[A, B]", list.toString());

        // continues to accept adds
        for (int i = 0; i < 50; i++) list.add("X" + i);
        assertEquals(sizeBefore + 50, list.size());
        assertEquals("X49", list.get(list.size() - 1));
    }

    @Test
    @DisplayName("trimToSize reduces backing storage (behavioral), size unchanged")
    void trimToSize_keepsSizeAndData() {
        CSArrayList<Integer> list = new CSArrayList<>();
        for (int i = 0; i < 100; i++) list.add(i);
        for (int i = 0; i < 80; i++) list.remove(0); // leave 20 elements: 80..99
        assertEquals(20, list.size());
        assertEquals(99, (int) list.get(19));

        list.trimToSize(); // behavior-only check (capacity not exposed)
        // should still work: reads and appends
        assertEquals(99, (int) list.get(19));
        list.add(777);
        assertEquals(21, list.size());
        assertEquals(777, (int) list.get(20));
    }


    @Test
    @DisplayName("indexOf and remove(Object) handle nulls and duplicates")
    void nullsAndDuplicates_indexOfAndRemove() {
        CSArrayList<String> list = new CSArrayList<>();
        list.add(null);
        list.add("A");
        list.add("A");
        list.add(null);

        assertEquals(0, list.indexOf(null));
        assertEquals(1, list.indexOf("A"));

        assertTrue(list.remove((Object) null)); // remove first null
        assertTrue(list.remove("A"));           // remove first "A"
        assertEquals("[A, null]", list.toString());

        // removing missing elements:
        assertFalse(list.remove("Z"));
        assertFalse(list.remove((Object) "not here"));
    }

    @Test
    @DisplayName("indexOf returns -1 when not present")
    void indexOf_notFound() {
        CSArrayList<Integer> list = new CSArrayList<>();
        list.add(10);
        list.add(20);
        assertEquals(-1, list.indexOf(30));
    }



    @Test
    @DisplayName("Iterator is fail-fast on structural modification")
    void iterator_isFailFast() {
        CSArrayList<Integer> list = new CSArrayList<>();
        for (int i = 0; i < 5; i++) list.add(i);

        Iterator<Integer> it = list.iterator(); // captures expectedModCount
        list.add(99);                           // structural modification -> modCount++

        // The check happens on next(), not hasNext()
        assertThrows(ConcurrentModificationException.class, it::next);
    }


    @Test
    @DisplayName("clear empties the list; reuse still works")
    void clear_makesEmpty() {
        CSArrayList<String> list = new CSArrayList<>();
        list.add("A");
        list.add("B");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertEquals("[]", list.toString());

        // re-use after clear
        list.add("C");
        assertEquals(1, list.size());
        assertEquals("[C]", list.toString());
    }


    @Test
    @DisplayName("toString uses [a, b, c] formatting")
    void toString_format() {
        CSArrayList<String> list = new CSArrayList<>();
        assertEquals("[]", list.toString());
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("[A, B, C]", list.toString());
    }
}


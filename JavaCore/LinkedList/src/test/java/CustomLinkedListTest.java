import innowise.java.core.CustomLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {

    private CustomLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    void testAddFirst() {
        list.addFirst(10);
        assertEquals(1, list.size());
        assertEquals(10, list.getFirst());
        assertEquals(10, list.getLast());

        list.addFirst(20);
        assertEquals(2, list.size());
        assertEquals(20, list.getFirst());
        assertEquals(10, list.getLast());
    }

    @Test
    void testAddLast() {
        list.addLast(5);
        assertEquals(1, list.size());
        assertEquals(5, list.getFirst());
        assertEquals(5, list.getLast());

        list.addLast(15);
        assertEquals(2, list.size());
        assertEquals(5, list.getFirst());
        assertEquals(15, list.getLast());
    }

    @Test
    void testAddByIndex() {
        list.addLast(1);
        list.addLast(3);
        list.add(1, 2);
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));

        list.add(0, 0);
        assertEquals(4, list.size());
        assertEquals(0, list.getFirst());

        list.add(list.size(), 4);
        assertEquals(5, list.size());
        assertEquals(4, list.getLast());
    }

    @Test
    void testGetFirstAndLast() {
        list.addLast(1);
        list.addLast(2);
        assertEquals(1, list.getFirst());
        assertEquals(2, list.getLast());
    }

    @Test
    void testGetByIndex() {
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    void testRemoveFirst() {
        list.addLast(10);
        list.addLast(20);
        int removed = list.removeFirst();
        assertEquals(10, removed);
        assertEquals(1, list.size());
        assertEquals(20, list.getFirst());
    }

    @Test
    void testRemoveLast() {
        list.addLast(10);
        list.addLast(20);
        int removed = list.removeLast();
        assertEquals(20, removed);
        assertEquals(1, list.size());
        assertEquals(10, list.getLast());
    }

    @Test
    void testRemoveByIndex() {
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        int removed = list.remove(1);
        assertEquals(20, removed);
        assertEquals(2, list.size());
        assertEquals(10, list.get(0));
        assertEquals(30, list.get(1));
    }

    @Test
    void testRemoveFirstSingleElement() {
        list.addFirst(5);
        int removed = list.removeFirst();
        assertEquals(5, removed);
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
    }

    @Test
    void testRemoveLastSingleElement() {
        list.addLast(7);
        int removed = list.removeLast();
        assertEquals(7, removed);
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
    }

    @Test
    void testGetNodeInvalidIndex() {
        list.addLast(1);
        list.addLast(2);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(5));
    }

    @Test
    void testGetFromEmptyListThrows() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    void testAddAndRemoveComplexSequence() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addFirst(0);
        list.remove(2);
        list.add(2, 5);

        assertEquals(4, list.size());
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(5, list.get(2));
        assertEquals(3, list.get(3));
    }
}

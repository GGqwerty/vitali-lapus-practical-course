package innowise.java.core;

import java.util.NoSuchElementException;

public class CustomLinkedList<T> {

    private int size = 0;

    private Node<T> first;
    private Node<T> last;

    public int size() {
        return size;
    }

    public void addFirst(T el) {
        Node<T> newNode = new Node<>(null, el, first);

        if (first == null) {
            last = newNode;
        } else {
            first.prev = newNode;
        }

        first = newNode;
        size++;
    }

    public void add(int index, T el) {
        if (index == size) {
            addLast(el);
            return;
        }
        if (index == 0) {
            addFirst(el);
            return;
        }

        Node<T> node = getNode(index);

        node.prev = new Node<>(node.prev, el, node);
        size++;
    }

    public void addLast(T el) {
        Node<T> newNode = new Node<>(last, el, null);

        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }

        last = newNode;
        size++;
    }

    public T getFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        return first.item;
    }

    public T get(int index) {
        return getNode(index).item;
    }

    public T getLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        return last.item;
    }

    public T removeFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        T el = first.item;
        Node<T> next = first.next;
        first.next = null;
        first.item = null;
        first = next;
        if (next == null) {
            last = null;
        } else {
            next.prev = null;
        }
        size--;
        return el;
    }

    public T remove(int index) {
        if (index == 0) {
            return removeFirst();
        }
        if (index == size - 1) {
            return removeLast();
        }

        Node<T> deleted = getNode(index);

        T el = deleted.item;
        deleted.prev.next = deleted.next;
        deleted.next.prev = deleted.prev;
        deleted.prev = null;
        deleted.next = null;
        deleted.item = null;
        size--;
        return el;
    }

    public T removeLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        T el = last.item;
        Node<T> prev = last.prev;
        last.prev = null;
        last.item = null;
        last = prev;
        if (prev == null) {
            first = null;
        } else {
            prev.next = null;
        }
        size--;
        return el;
    }

    private Node<T> getNode(int index) {
        Node<T> iterator;
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index < (size >> 1)) {
            iterator = first;
            for (int i = 0; i < index; i++) {
                if (iterator.next == null) {
                    throw new IndexOutOfBoundsException();
                }
                iterator = iterator.next;
            }
        } else {
            iterator = last;
            for (int i = size - 1; i > index; i--) {
                if (iterator.prev == null) {
                    throw new IndexOutOfBoundsException();
                }
                iterator = iterator.prev;
            }
        }
        if (iterator == null) {
            throw new NoSuchElementException();
        }
        return iterator;
    }

    private static class Node<T> {
        T item;

        Node<T> next;
        Node<T> prev;

        Node(Node<T> prev, T item, Node<T> next) {
            this.prev = prev;
            this.item = item;
            this.next = next;
        }
    }
}

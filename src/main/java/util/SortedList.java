package util;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class SortedList<E extends Comparable<? super E>> implements Iterable<E>, Collection<E> {
    private LinkedList<E> list;

    public SortedList() {
        list = new LinkedList<>();
    }

    public SortedList(Collection<? extends E> c) {
        list = new LinkedList<>();
        addAll(c);
    }

    public boolean add(E element) {
        int i;
        for (i = 0; i < list.size(); i++) {
            if (element.compareTo(list.get(i)) < 0) {
                list.add(i, element);
                return true;
            }
        }
        list.add(element);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        for (E element : c) {
            add(element);
        }
        return true;
    }

    public void clear() {
        list.clear();
    }

    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return list.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return list.equals(o);
    }

    public E get(int index) {
        return list.get(index);
    }

    public int indexOf(Object o) {
        //noinspection SuspiciousMethodCalls
        return list.indexOf(o);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<E> iterator() {
        return list.iterator();
    }

    public E remove(int index) {
        return list.remove(index);
    }

    public boolean removeAll(Collection<?> c) {
        //noinspection SuspiciousMethodCalls
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        //noinspection SuspiciousMethodCalls
        return list.retainAll(c);
    }

    public boolean remove(Object element) {
        //noinspection SuspiciousMethodCalls
        return list.remove(element);
    }

    public int size() {
        return list.size();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }




}

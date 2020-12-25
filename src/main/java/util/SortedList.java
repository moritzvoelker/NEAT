/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

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

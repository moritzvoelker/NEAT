package neat;

import java.util.LinkedList;

public class TestList<T> extends LinkedList<T> {
    @Override
    public boolean add(T t) {
        if (contains(t)) {
            System.out.println("Stop in add");
        }
        return super.add(t);
    }

    @Override
    public boolean contains(Object o) {
        boolean contains = super.contains(o);
        if (contains) {
            System.out.println("Stop in contains");
        }
        return contains;
    }

    @Override
    public boolean remove(Object o) {
        if (size() == 1) {
            System.out.println("Removing though we should not");
        }
        return super.remove(o);
    }

}

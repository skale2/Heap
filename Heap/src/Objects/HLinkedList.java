package Objects;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class HLinkedList extends HList {

    public HLinkedList() {
        _list = new LinkedList<>();
    }

    public HLinkedList(LinkedList<Any> list) {
        super(list);
    }

    static List<Any> getList() { return new LinkedList<>(); }

    static HList createHList(LinkedList<Any> list) { return new HLinkedList(list); }

    public static final Type type = new Type("LINKEDLIST");

    static class Iter extends Iterator {
        public Iter(HLinkedList list) {
            super();
            _listIterator = list.list().listIterator();
        }

        @Override
        public Any next() {
            return _listIterator.next();
        }

        @Override
        public Bool hasNext() {
            return Bool.valueOf(_listIterator.hasNext());
        }

        private ListIterator<Any> _listIterator;
    }
}

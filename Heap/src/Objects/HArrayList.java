package Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class HArrayList extends HList {

    public HArrayList() {
        _list = new ArrayList<>();
    }

    public HArrayList(ArrayList<Any> list) {
        super(list);
    }

    static List<Any> getList() { return new ArrayList<>(); }

    static HList createHList(ArrayList<Any> list) { return new HArrayList(list); }

    public static final Type type = new Type("ARRAYLIST");

    static class Iter extends Iterator {
        public Iter(HArrayList list) {
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

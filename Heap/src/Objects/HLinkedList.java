package Objects;

import java.util.LinkedList;
import java.util.List;

public class HLinkedList extends HList {

    public HLinkedList() {
        _list = new LinkedList<>();
    }

    public HLinkedList(LinkedList<Any> list) {
        super(list);
    }

    static List<Any> getList() { return new LinkedList<>(); }

    static HList createHList(LinkedList<Any> list) { return new HLinkedList(list); }

    private static final Type type = new Type("LINKEDLIST");
}

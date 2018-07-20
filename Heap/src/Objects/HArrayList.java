package Objects;

import java.util.ArrayList;
import java.util.List;

public class HArrayList extends HList {

    public HArrayList() {
        _list = new ArrayList<>();
    }

    public HArrayList(ArrayList<Any> list) {
        super(list);
    }

    static List<Any> getList() { return new ArrayList<>(); }

    static HList createHList(ArrayList<Any> list) { return new HArrayList(list); }

    private static final Type type = new Type("ARRAYLIST");
}

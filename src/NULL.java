public class NULL {
    private static NULL ourInstance = new NULL();

    public static NULL getInstance() {
        return ourInstance;
    }

    private NULL() {

    }
}

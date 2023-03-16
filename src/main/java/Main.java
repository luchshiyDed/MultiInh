import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ExtendedClassFabric extendedClassFabric =new ExtendedClassFabric();
        ArrayList<Object[]>generalArray= new ArrayList<>();
        Frog frog = (Frog) extendedClassFabric.createObject(Frog.class,generalArray);
        frog.say();
    }
}

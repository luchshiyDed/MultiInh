import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ClassConstructorFinder classConstructorFinder=new ClassConstructorFinder();
        ArrayList<Object[]>generalArray= new ArrayList<>();
        generalArray.add(new String[]{"kva"});
        generalArray.add(new String[]{"auf"});
        //generalArray.add(new Object[]{});
        Object[] frog = classConstructorFinder.createClass(Frog.class,generalArray);
    }
}

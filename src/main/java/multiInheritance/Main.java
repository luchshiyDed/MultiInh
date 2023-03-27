package multiInheritance;

import demoClasses.Frog;
import demoClasses.SomeInterface;

import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {
        ExtendedClassFabric extendedClassFabric =new ExtendedClassFabric(SomeInterface.class, Frog.class);
        ArrayList<Object[]>generalArray= new ArrayList<>();
        generalArray.add(new Object[]{});
        generalArray.add(new String[]{"awe"});
        Frog frog = (Frog) extendedClassFabric.create(generalArray);
        frog.say();
    }
}

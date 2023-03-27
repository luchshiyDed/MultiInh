package multiInheritance;

import demoClasses.Frog;
import multiInheritance.ExtendedClassFabric;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ExtendedClassFabric extendedClassFabric =new ExtendedClassFabric();
        ArrayList<Object[]>generalArray= new ArrayList<>();
        generalArray.add(new Object[]{});
        generalArray.add(new String[]{"awe"});
        Frog frog = (Frog) extendedClassFabric.createObject(Frog.class,generalArray);
        frog.say();
    }
}

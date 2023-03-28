package demoClasses;

import multiInheritance.ExtendsAll;

import java.util.Stack;

@ExtendsAll(classes = {LandAnimal.class, WaterAnimal.class})
public class Frog implements SomeInterface {

    public Frog(){

    }

    @Override
    public Object getObject(int i) {
        return null;
    }

    @Override
    public void say() {
        System.out.println("I am a frog");
    }

    @Override
    public String returnAString() {
        return "f";
    }

    @Override
    public void sound(String str) {
        System.out.println("I am a frog I ignored the parameter and said:kva");
    }
}
package demoClasses;

import multiInheritance.ExtendsAll;

import java.util.Stack;

@ExtendsAll(classes = {LandAnimal.class, WaterAnimal.class})
public class Frog implements SomeInterface {

    public Frog(){

    }

    @Override
    public void say() {
        System.out.println("I am a frog");
    }

    @Override
    public void move() {
        System.out.println("skok skok");
    }

    @Override
    public void sound(String str) {
        System.out.println("sva");
    }
}
package demoClasses;

import demoClasses.Animal;
import multiInheritance.ExtendsAll;

@ExtendsAll(classes = {Animal.class})
public class WaterAnimal implements SomeInterface {
    public WaterAnimal(){
    }
    @Override
    public void say(){
        System.out.println("I live in water");
    }

    @Override
    public String returnAString() {
        return "w";
    }

    @Override
    public void sound(String str) {
        System.out.println("I am a water animal i got the parameter from parameters i said:"+str);
    }

}

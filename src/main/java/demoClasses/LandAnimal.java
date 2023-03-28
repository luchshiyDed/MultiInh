package demoClasses;

import demoClasses.Animal;
import multiInheritance.ExtendsAll;

@ExtendsAll(classes = {Animal.class})
public class LandAnimal implements SomeInterface {
    String str;
    public LandAnimal(String str){
        this.str=str;
    }
    @Override
    public void say(){
        System.out.println("I live on the ground");
    }

    @Override
    public void move() {
        System.out.println("I walk");
    }

    @Override
    public void sound(String str) {
        System.out.println("I am a land animal i got the parameter from my constructor and said:"+this.str);
    }

}

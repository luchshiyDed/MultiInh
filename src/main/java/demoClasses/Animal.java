package demoClasses;

public class Animal implements SomeInterface {
    public Animal(){
    }
    @Override
    public void say(){
        System.out.println("I live");
    }

    @Override
    public void move() {

    }

    @Override
    public void sound(String str) {
        System.out.println("I am an animal I can't talk");
    }
}

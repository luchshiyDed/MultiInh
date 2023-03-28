package demoClasses;

public class Animal implements SomeInterface {
    public Animal(){
    }

    @Override
    public Object getObject(int i) {
        return null;
    }

    @Override
    public void say(){
        System.out.println("I live");
    }

    @Override
    public String returnAString() {
        return "a";
    }

    @Override
    public void sound(String str) {
        System.out.println("I am an animal I can't talk");
    }
}

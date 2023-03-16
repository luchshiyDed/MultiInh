@ExtendsAll(classes = {Animal.class})
public class WaterAnimal implements SomeInterface {
    public WaterAnimal(){
    }
    @Override
    public void say(){
        System.out.println("I live in water");
    }

    @Override
    public void move() {
        System.out.println("I swim");
    }

    @Override
    public void sound(String str) {
        System.out.println(str);
    }

}

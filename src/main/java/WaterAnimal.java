@ExtendsAll(classes = {Animal.class})
public class WaterAnimal implements SomeInterface {
    public WaterAnimal(){
    }
    @Override
    public void say(){
        System.out.println("I live in water");
    }
    public void swim(){
        System.out.println("I swim");
    }
}

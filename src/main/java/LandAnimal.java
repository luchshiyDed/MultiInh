@ExtendsAll(classes = {Animal.class})
public class LandAnimal implements SomeInterface {
    public LandAnimal(){
    }
    @Override
    public void say(){
        System.out.println("I live on the ground");
    }
    public void walk(){
        System.out.println("I walk");
    }
}

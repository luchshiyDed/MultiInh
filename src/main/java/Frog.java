import java.util.Stack;

@ExtendsAll(classes = {Animal.class, Stack.class})
public class Frog {

    public Frog(String str){
        System.out.println(str);
    }
    public void say(){
        System.out.println("Да");
    }
}
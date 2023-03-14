import io.activej.codegen.DefiningClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class ClassConstructorFinder{
    public Object[] createClass(Class<?> aCLass, ArrayList<Object[]> parameters) {
        ArrayList<Class<?>[]> parametersTypes = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            Class<?>[] array = new Class[parameters.get(i).length];
            parametersTypes.add(i, array);
            for (int j = 0; j < parameters.get(i).length; j++) {
                array[j] = parameters.get(i)[j].getClass();
            }
        }
        Object[] compositionObjects=new Object[aCLass.getAnnotation(ExtendsAll.class).classes().length+1];
        try {
            compositionObjects[0]=aCLass.getConstructor(parametersTypes.get(0)).newInstance(parameters.get(0));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println(Arrays.toString(parametersTypes.get(0)));
            e.printStackTrace();

        }
        for(int i = 0; i < aCLass.getAnnotation(ExtendsAll.class).classes().length; i++) {
            try {
                if(i+1>=parameters.size())
                    compositionObjects[i+1] = aCLass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor().newInstance();
                else
                    compositionObjects[i+1] = aCLass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor(parametersTypes.get(i+1)).newInstance(parameters.get(i+1));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        Class<Greeter> greeterClass = ClassBuilder.create(Greeter.class).withMethod("sayHello",call(staticField(System.class, "out"),"println", value("Hello world")))        .defineClass(classLoader);
        return compositionObjects;
    }
}



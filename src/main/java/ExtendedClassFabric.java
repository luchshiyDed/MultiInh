import io.activej.codegen.ClassBuilder;
import io.activej.codegen.DefiningClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static io.activej.codegen.expression.Expressions.*;


public class ExtendedClassFabric {

    public ExtendedClassFabric(){
    }
    // чтобы нормально добавлять методы в билдер класса делаем билдер для Expressionov
    private class ActiveJExpessionBuilder{

    }

    public Object createObject(Class<?> aCLass, ArrayList<Object[]> parameters) {
        ArrayList<Class<?>[]> parametersTypes = new ArrayList<>();
        Class<?> aInterface = null;
        for (int i = 0; i < aCLass.getInterfaces().length; i++) {
            if (aCLass.getInterfaces()[i].isAnnotationPresent(RootInterface.class)) {
                aInterface = aCLass.getInterfaces()[i];
            }
        }
        assert aInterface != null;

        for (int i = 0; i < parameters.size(); i++) {
            Class<?>[] array = new Class[parameters.get(i).length];
            parametersTypes.add(i, array);
            for (int j = 0; j < parameters.get(i).length; j++) {
                array[j] = parameters.get(i)[j].getClass();
            }
        }
        ArrayList<SomeInterface> compositionObjects = new ArrayList<>(aCLass.getAnnotation(ExtendsAll.class).classes().length);
        try {
            if(parametersTypes.size()>0)
                compositionObjects.add(0, (SomeInterface) aCLass.getConstructor(parametersTypes.get(0)).newInstance(parameters.get(0)));
            else
                compositionObjects.add(0, (SomeInterface) aCLass.getConstructor().newInstance());
        }

        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println(Arrays.toString(parametersTypes.get(0)));
            e.printStackTrace();
        }

        for (int i = 0; i < aCLass.getAnnotation(ExtendsAll.class).classes().length; i++) {
            try {
                if (i + 1 >= parameters.size())
                    compositionObjects.add(i+1, (SomeInterface) aCLass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor().newInstance());
                else
                    compositionObjects.add(i+1, (SomeInterface) aCLass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor(parametersTypes.get(i + 1)).newInstance(parameters.get(i + 1)));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        ClassBuilder<?> builder = ClassBuilder.create(aCLass);
        builder=builder.withField("objects",compositionObjects.getClass(),value(compositionObjects));
        builder=builder.withMethod("say",call(cast(call(property(self(),"objects"),"get",value(1)),SomeInterface.class)/*вот ето будет в билдере expressions*/,"say"));
        builder=builder.withMethod("say",call(cast(call(property(self(),"objects"),"get",value(2)),SomeInterface.class),"say"));
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
        //for (Method method : aInterface.getMethods()) {
            //}
    }
}



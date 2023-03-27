package multiInheritance;

import io.activej.codegen.ClassBuilder;
import io.activej.codegen.DefiningClassLoader;
import io.activej.codegen.expression.Expression;

import java.lang.reflect.Method;
import java.util.*;

import static io.activej.codegen.expression.Expressions.*;


public class ExtendedClassFabric {
    private HashMap<String, ArrayList<Expression>> methods;
    private ClassBuilder<?> builder;
    private Stack<ExtendedClassFabric> stack;
    private Class<?> rootInterface;
    private ArrayList<Object> compositionObjects;
    private Class<?> extendibleClass;

    // Creates an expression for addExpression i - index in compositionObjects, method - called method
    private Expression createMethodExpr(Integer i, Method method){
        ArrayList<Expression> variables = new ArrayList<>();
        for (int j = 0; j < method.getParameterCount(); j++)
            variables.add(arg(j));
        if (variables.size() > 0)
            return call(cast(call(property(self(), "objects"), "get", value(i)), this.rootInterface), method.getName(), sequence(variables));
        return call(cast(call(property(self(), "objects"), "get", value(i)), this.rootInterface), method.getName());

    }

    // add expression to the method body
    private void addExpression(String methodName, Expression expression) {
        methods.get(methodName).add(expression);
    }

    // creates the method and adds it to the final class
    private ClassBuilder<?> createAndAddMethod(String methodName, Class<?> returnType, List<Class<?>> parameters) {
        builder = builder.withMethod(methodName, returnType, parameters, sequence(methods.get(methodName)));
        return builder;
    }

    public Object create(ArrayList<Object[]> parameters) {
        ArrayList<Class<?>[]> parametersTypes = new ArrayList<>();

        for (int i = 0; i < parameters.size(); i++) {
            Class<?>[] array = new Class[parameters.get(i).length];
            parametersTypes.add(i, array);
            for (int j = 0; j < parameters.get(i).length; j++) {
                array[j] = parameters.get(i)[j].getClass();
            }
        }
        this.compositionObjects=new ArrayList<>();
        //TODO: createObjects через стек
        builder.withField("objects", parameters.getClass());
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
    }

    public ExtendedClassFabric(Class<?> rootInterface, Class<?> aClass) {
        methods = new HashMap<>();
        this.rootInterface = rootInterface;
        builder = ClassBuilder.create(rootInterface);
        this.extendibleClass=aClass;
    }
}



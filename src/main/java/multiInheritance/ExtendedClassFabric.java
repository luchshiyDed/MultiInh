package multiInheritance;

import io.activej.codegen.ClassBuilder;
import io.activej.codegen.DefiningClassLoader;
import io.activej.codegen.expression.Expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.activej.codegen.expression.Expressions.*;


public class ExtendedClassFabric {
    private HashMap<String, ArrayList<Expression>> methods;
    private ClassBuilder<?> builder;
    private Stack<Class<?>> stack;
    private Stack<Object[]> stackForParameters;
    private final Class<?> rootInterface;
    private ArrayList<Object> compositionObjects;
    private final Class<?> extendibleClass;

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
        methods.computeIfAbsent(methodName, k -> new ArrayList<>());
        methods.get(methodName).add(expression);
    }

    // creates the method and adds it to the final class
    private ClassBuilder<?> createAndAddMethod(String methodName, Class<?> returnType, List<Class<?>> parameters) {
        builder = builder.withMethod(methodName, returnType, parameters, sequence(methods.get(methodName)));
        return builder;
    }

    /***
     *
     * @param parameters each element is an input parameter for a super-class
     * @return object extended by classes
     */
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
        fillStack(extendibleClass, parameters, 0);
        try {
            createObjectsFromStack();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        builder.withField("objects", compositionObjects.getClass(),value(compositionObjects));
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
    }

    private void fillStack(Class<?> currentClass,ArrayList<Object[]> parameters, int i ){
        int j = 0;
        stack = new Stack<>();
        Queue<Class<?>> queue = new LinkedList<>();
        stackForParameters = new Stack<>();
        Class<?> link = currentClass;
        queue.add(link);
        while (!queue.isEmpty()){
            Class<?> u = queue.remove();
            stack.push(u);
            if (j<parameters.size()){
                stackForParameters.push(parameters.get(j));
            }
            else{
                stackForParameters.push(new Object[]{});
            }
            j++;
            if (u.isAnnotationPresent(ExtendsAll.class)) {
                Class<?>[]  localObjects = u.getAnnotation(ExtendsAll.class).classes();
                for (Class<?> classer: localObjects) {
                    queue.add(classer);
                }
            }
        }

    }

    private void createObjectsFromStack() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int size = stack.size();
        for (int j = 0; j< size; j++) {
            while(stackForParameters.size() < stack.size()){
                compositionObjects.add(j, stack.pop().getConstructor().newInstance());
            }
            Object[] array = stackForParameters.pop();
            Class<?>[] parameters = new Class[array.length];
            for (int i = 0; i < array.length; i++) {
                parameters[i] = array[i].getClass();
            }
            compositionObjects.add(j, stack.pop().getConstructor(parameters).newInstance(array));
            for (Method method: rootInterface.getMethods()) {
                addExpression(method.getName(), createMethodExpr(j,method));
            }
        }
        for (Method method: rootInterface.getMethods()) {
            createAndAddMethod(method.getName(), method.getReturnType(), Arrays.asList(method.getParameterTypes()));
        }

    }

    public ExtendedClassFabric(Class<?> rootInterface, Class<?> aClass) {
        this.methods = new HashMap<>();
        this.rootInterface = rootInterface;
        this.builder = ClassBuilder.create(aClass);
        this.extendibleClass=aClass;
    }

    public Object createObject(ArrayList<Object[]> parameters) {
        ArrayList<Class<?>[]> parametersTypes = new ArrayList<>();

        for (int i = 0; i < parameters.size(); i++) {
            Class<?>[] array = new Class[parameters.get(i).length];
            parametersTypes.add(i, array);
            for (int j = 0; j < parameters.get(i).length; j++) {
                array[j] = parameters.get(i)[j].getClass();
            }
        }
        ArrayList<Object> compositionObjects = new ArrayList(this.extendibleClass.getAnnotation(ExtendsAll.class).classes().length);
        try {
            if (parametersTypes.size() > 0)
                compositionObjects.add(0, this.extendibleClass.getConstructor(parametersTypes.get(0)).newInstance(parameters.get(0)));
            else
                compositionObjects.add(0, this.extendibleClass.getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.out.println(Arrays.toString(parametersTypes.get(0)));
            e.printStackTrace();
        }

        for (int i = 0; i < this.extendibleClass.getAnnotation(ExtendsAll.class).classes().length; i++) {
            try {
                if (i + 1 >= parameters.size())
                    compositionObjects.add(i + 1, this.extendibleClass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor().newInstance());
                else
                    compositionObjects.add(i + 1,  this.extendibleClass.getAnnotation(ExtendsAll.class).classes()[i].getConstructor(parametersTypes.get(i + 1)).newInstance(parameters.get(i + 1)));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        ClassBuilder<?> builder = ClassBuilder.create(this.extendibleClass);
        builder = builder.withField("objects", compositionObjects.getClass(), value(compositionObjects));
        for (Method method : this.rootInterface.getMethods()) {
            ArrayList<Expression> methodsExpressions = new ArrayList<>();
            for (int i = 0; i < compositionObjects.size(); i++) {
                try {
                    compositionObjects.get(i).getClass().getMethod(method.getName(), method.getParameterTypes());
                    ArrayList<Expression> variables = new ArrayList<>();
                    for (int j = 0; j < method.getParameterCount(); j++) {
                        variables.add(arg(j));
                    }
                    if (variables.size() > 0)
                        methodsExpressions.add(call(cast(call(property(self(), "objects"), "get", value(i)), this.rootInterface), method.getName(), sequence(variables)));
                    else
                        methodsExpressions.add(call(cast(call(property(self(), "objects"), "get", value(i)), this.rootInterface), method.getName()));
                } catch (NoSuchMethodException ignored) {
                }
            }
            builder = builder.withMethod(method.getName(), method.getReturnType(), Arrays.asList(method.getParameterTypes()), sequence(methodsExpressions));
        }

        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
    }
}



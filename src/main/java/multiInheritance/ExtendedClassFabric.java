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
    private final Class<?> rootInterface;
    private ArrayList<Object> compositionObjects;
    private final Class<?> extendibleClass;
    private Stack<Object[]> stackForParameters;
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
        builder.withMethod(methodName, returnType, parameters, sequence(methods.get(methodName)));
        return builder;
    }
    private void interfaceCheck(){
        try {
            rootInterface.getMethod("getObject",int.class);
        } catch ( NullPointerException e) {
            System.err.println("No rootInterfaceFound");
            e.printStackTrace();
        }
        catch (NoSuchMethodException e){
            System.err.println("rootInterface must contain an empty <public Object getObject(int)> method");
            e.printStackTrace();
        }
    }
    private void fillStack(Class<?> currentClass, ArrayList<Object[]> parameters) {
        int j = 0;
        stack = new Stack<>();
        Queue<Class<?>> queue = new LinkedList<>();
        stackForParameters = new Stack<>();
        queue.add(currentClass);
        while (!queue.isEmpty()) {
            Class<?> u = queue.remove();
            stack.push(u);
            if (j < parameters.size()) {
                stackForParameters.push(parameters.get(j));
            } else {
                stackForParameters.push(new Object[]{});
            }
            j++;
            if (u.isAnnotationPresent(ExtendsAll.class)) {
                Class<?>[] localObjects = u.getAnnotation(ExtendsAll.class).classes();
                queue.addAll(Arrays.asList(localObjects));
            }
        }
    }

    private void createObjectsFromStack() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int size = stack.size();
        for (int j = 0; j< size; j++) {
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
    /***
     *
     * @param parameters each element is an input parameter for a super-class
     * @return object extended by classes
     */
    public Object create(ArrayList<Object[]> parameters) {
        this.interfaceCheck();
        this.compositionObjects=new ArrayList<>();
        this.builder = ClassBuilder.create(this.extendibleClass);
        fillStack(extendibleClass, parameters);
        try {
            createObjectsFromStack();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

        }
        builder.withField("objects", compositionObjects.getClass(),value(compositionObjects));
        builder.withMethod("getObject",Object.class,Arrays.asList(new Class<?>[]{int.class}),call(property(self(), "objects"), "get",arg(0)));
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
    }

    public ExtendedClassFabric(Class<?> aClass) {
        Class<?> rootInterface1;
        this.methods = new HashMap<>();
        rootInterface1 =null;
        for (Class<?> interfac:aClass.getInterfaces()) {
            if(interfac.isAnnotationPresent(RootInterface.class))
                rootInterface1 =interfac;
        }
        this.rootInterface = rootInterface1;
        interfaceCheck();
        this.extendibleClass=aClass;
    }

    /**
     * creates an 1 level extended object, only the classes in ExtendAll will be extended.
     * @param parameters input array of parameters 0 index for main class,
     *                   the order corresponds with the order in ExtendAll annotation if there is no parameters provided
     *                   the fabric will try to use no-parameters constructor
     * @return object
     */
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
        builder.withField("objects", compositionObjects.getClass(), value(compositionObjects));
        for (Method method : this.rootInterface.getMethods()) {
            ArrayList<Expression> methodsExpressions = new ArrayList<>();
            for (int i = compositionObjects.size()-1; i >= 0 ; i--) {
                methodsExpressions.add(this.createMethodExpr(i,method));
            }
            builder.withMethod(method.getName(), method.getReturnType(), Arrays.asList(method.getParameterTypes()), sequence(methodsExpressions));
        }
        builder.withMethod("getObject",Object.class,Arrays.asList(new Class<?>[]{int.class}),call(property(self(), "objects"), "get",arg(0)));
        DefiningClassLoader classLoader = DefiningClassLoader.create();
        return builder.defineClassAndCreateInstance(classLoader);
    }
}



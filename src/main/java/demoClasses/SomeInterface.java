package demoClasses;

import multiInheritance.RootInterface;

@RootInterface
public interface SomeInterface {
    Object getObject(int i);
    void say();
    String returnAString();
    void sound(String str);
}

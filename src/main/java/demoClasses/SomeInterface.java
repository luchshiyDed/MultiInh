package demoClasses;

import multiInheritance.RootInterface;

import java.util.ArrayList;
@RootInterface
public interface SomeInterface {
    Object getObject(int i);
    void say();
    String returnAString();
    void sound(String str);
}

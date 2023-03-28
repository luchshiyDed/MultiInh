
import demoClasses.Frog;
import demoClasses.SomeInterface;
import multiInheritance.ExtendedClassFabric;
import multiInheritance.ExtendsAll;
import multiInheritance.RootInterface;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.assertThrows;

public class GeneralTest {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void testString() {
        System.setOut(new PrintStream(output));
        ExtendedClassFabric extendedClassFabric =new ExtendedClassFabric(Frog.class);
        ArrayList<Object[]> generalArray= new ArrayList<>();
        generalArray.add(new Object[]{});
        generalArray.add(new String[]{"awe"});
        Frog frog = (Frog) extendedClassFabric.create(generalArray);
        frog.say();
        Assert.assertEquals("I live"+System.getProperty("line.separator")+"I live"+System.getProperty("line.separator")+"I live in water"+System.getProperty("line.separator")+"I live on the ground"+System.getProperty("line.separator")+"I am a frog"+System.getProperty("line.separator")
                , output.toString());
        System.setOut(null);
    }

}

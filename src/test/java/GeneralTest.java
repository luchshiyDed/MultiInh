
import demoClasses.Frog;
import demoClasses.SomeInterface;
import multiInheritance.ExtendedClassFabric;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class GeneralTest {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

    @Test
    public void testString() {
        ExtendedClassFabric extendedClassFabric =new ExtendedClassFabric(SomeInterface.class, Frog.class);
        ArrayList<Object[]> generalArray= new ArrayList<>();
        generalArray.add(new Object[]{});
        generalArray.add(new String[]{"awe"});
        Frog frog = (Frog) extendedClassFabric.createObject(generalArray);
        frog.say();
        Assert.assertEquals("I live in water"+System.getProperty("line.separator")+"I live on the ground"+System.getProperty("line.separator")+"I am a frog"+System.getProperty("line.separator")
                , output.toString());
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

}

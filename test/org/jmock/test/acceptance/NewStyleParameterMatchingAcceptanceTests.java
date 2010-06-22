package org.jmock.test.acceptance;

import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.ExpectationError;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;

public class NewStyleParameterMatchingAcceptanceTests extends TestCase {
    public interface AnInterface {
        void doSomethingWith(String s);
        void doSomethingWithBoth(String s1, String s2);
        
        void doSomethingWithBoth(boolean i1, boolean i2);
        void doSomethingWithBoth(byte i1, byte i2);
        void doSomethingWithBoth(short i1, short i2);
        void doSomethingWithBoth(char c1, char c2);
        void doSomethingWithBoth(int i1, int i2);
        void doSomethingWithBoth(long i1, long i2);
        void doSomethingWithBoth(float i1, float i2);
        void doSomethingWithBoth(double i1, double i2);
        
        void beSilly(List<List<Set<String>>> silly);
    }
    
    Mockery context = new Mockery();
    AnInterface mock = context.mock(AnInterface.class, "mock");
    
    public void testMatchesParameterValues() {
        context.checking(new Expectations() {{
            oneOf (mock).doSomethingWith(with.<String>is(equal("hello")));
            oneOf (mock).doSomethingWith(with.<String>is(equal("goodbye")));
        }});
        
        mock.doSomethingWith("hello");
        mock.doSomethingWith("goodbye");
        
        context.assertIsSatisfied();
    }
    
    public void testDoesNotAllowUnexpectedParameterValues() {
        context.checking(new Expectations() {{
            oneOf (mock).doSomethingWith(with.<String>is(equal("hello")));
            oneOf (mock).doSomethingWith(with.<String>is(equal("goodbye")));
        }});
        
        try {
            mock.doSomethingWith("hello");
            mock.doSomethingWith("Goodbye");
            fail("should have thrown ExpectationError");
        }
        catch (ExpectationError expected) {}
    }
    
    public void testAllOrNoneOfTheParametersMustBeSpecifiedByMatchers() {
        try {
            context.checking(new Expectations() {{
                oneOf (mock).doSomethingWithBoth(with.<String>is(equal("a-matcher")), "not-a-matcher");
            }});
        }
        catch (IllegalArgumentException expected) {
        }
    }
    
    // Test to show that issue JMOCK-160 is spurious
    public void testNotAllExpectationsOfSameMockMustUseMatchers() {
        context.checking(new Expectations() {{
            oneOf (mock).doSomethingWithBoth(with.<String>is(equal("x")), with.<String>is(equal("y")));
            oneOf (mock).doSomethingWith("z");
        }});
        
        mock.doSomethingWithBoth("x", "y");
        mock.doSomethingWith("z");
        
        context.assertIsSatisfied();
    }
    
    // See issue JMOCK-161
    public void testCanPassLiteralValuesToWithMethodToMeanEqualTo() {
        context.checking(new Expectations() {{
            exactly(2).of (mock).doSomethingWithBoth(with.<String>is(anything()), with("y"));
        }});
        
        mock.doSomethingWithBoth("x", "y");
        mock.doSomethingWithBoth("z", "y");
        
        context.assertIsSatisfied();
    }
    
    // See issue JMOCK-161
    public void testCanPassLiteralPrimitiveValuesToWithMethodToMeanEqualTo() {
        context.checking(new Expectations() {{
            exactly(2).of (mock).doSomethingWithBoth(with.booleanIs(anything()), with(true));
            exactly(2).of (mock).doSomethingWithBoth(with.byteIs(anything()), with((byte)1));
            exactly(2).of (mock).doSomethingWithBoth(with.shortIs(anything()), with((short)2));
            exactly(2).of (mock).doSomethingWithBoth(with.charIs(anything()), with('x'));
            exactly(2).of (mock).doSomethingWithBoth(with.intIs(anything()), with(3));
            exactly(2).of (mock).doSomethingWithBoth(with.longIs(anything()), with(4L));
            exactly(2).of (mock).doSomethingWithBoth(with.floatIs(anything()), with(5.0f));
            exactly(2).of (mock).doSomethingWithBoth(with.doubleIs(anything()), with(6.0));
        }});
        
        mock.doSomethingWithBoth(true, true);
        mock.doSomethingWithBoth(false, true);
        
        mock.doSomethingWithBoth((byte)1, (byte)1);
        mock.doSomethingWithBoth((byte)2, (byte)1);
        
        mock.doSomethingWithBoth((short)1, (short)2);
        mock.doSomethingWithBoth((short)2, (short)2);
        
        mock.doSomethingWithBoth('1', 'x');
        mock.doSomethingWithBoth('2', 'x');
        
        mock.doSomethingWithBoth(1, 3);
        mock.doSomethingWithBoth(2, 3);
        
        mock.doSomethingWithBoth(1L, 4L);
        mock.doSomethingWithBoth(2L, 4L);
        
        mock.doSomethingWithBoth(1.0f, 5.0f);
        mock.doSomethingWithBoth(2.0f, 5.0f);
        
        mock.doSomethingWithBoth(1.0, 6.0);
        mock.doSomethingWithBoth(2.0, 6.0);
        
        context.assertIsSatisfied();
    }
    
    public void ridiculousJavaTypeName() {
        final List<List<Set<String>>> silly = Collections.emptyList();
        
        context.checking(new Expectations() {{
            oneOf (mock).beSilly(with.<List<List<Set<String>>>>is(empty()));
        }});
    }
}
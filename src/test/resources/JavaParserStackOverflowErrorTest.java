
class SomeType {

    public FooType getFoo() {
        return new FooType();
    }

}

class FooType {

    public BarType getBar() {
        return new BarType();
    }

}

class BarType {

    class SomeAnotherType {

        @Override
        public String toString() {
            return "Hello, Bug!";
        }

    }

    @Override
    public String toString() {
        return "Instance of BarType class.";
    }

}


public class JavaParserStackOverflowErrorTest {

    public static void main(String... args) {
        Object result = test(new SomeType());
        System.out.println(result);
    }

    public static Object test(SomeType value) {
        return value.getFoo().getBar().new SomeAnotherType();
    }

}

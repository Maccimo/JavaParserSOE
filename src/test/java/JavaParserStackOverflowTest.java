import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class JavaParserStackOverflowTest {

    @BeforeAll
    public static void beforeAll() {
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
    }

    @Test
    public void testUnsolvedSymbolException() {
        doTest(
          "public class JavaParserStackOverflowErrorTest {\n" +
          "\n" +
          "    public static Object test(SomeType value) {\n" +
          "        return value.getFoo().getBar();\n" +
          "    }\n" +
          "\n" +
          "}\n"
        );
    }

    @Test
    public void testStackOverflowError() {
        doTest(
            "public class JavaParserStackOverflowErrorTest {\n" +
            "\n" +
            "    public static Object test(SomeType value) {\n" +
            "        return value.getFoo().getBar().new SomeAnotherType();\n" +
            "    }\n" +
            "\n" +
            "}\n"
        );
    }

    private void doTest(String sourceCode) {
        Assertions.assertThrows(
            UnsolvedSymbolException.class,
            () -> {
                JavaSymbolSolver symbolSolver = new JavaSymbolSolver(new ReflectionTypeSolver());
                StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

                CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

                MethodCallExpr methodCallExpr = compilationUnit
                    .findFirst(MethodCallExpr.class)
                    .orElseThrow(
                        () -> new AssertionFailedError("MethodCallExpr not found!")
                    );

                Assertions.assertEquals("value.getFoo().getBar()", methodCallExpr.toString());

                ResolvedMethodDeclaration methodDeclaration = methodCallExpr.resolve();

                Assertions.assertNotNull(methodDeclaration);
            }
        );
    }
}

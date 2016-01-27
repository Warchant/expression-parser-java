import org.junit.Test;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 */
public class Tests {
    @Test
    public void testParentAndChilds() throws Exception {
        for (int degree = 2; degree < 10; degree++) {
            MyTree<Integer> tree = new MyTree<>(degree);
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < degree; j++) {
                    int child = tree.jthChild(i, j);
                    int parent = tree.parent(child);
                    // System.out.println("Child: " + child + ", parent: " + parent);
                    if (parent != i) {
                        throw new Exception("TEST 1. Degree: " + degree + ", i: " + i + ", j: " + j);
                    }
                }
            }
        }
    }

    @Test
    public void testExpression1() throws Exception {
        double r;

        Expression p = new Expression("1+2+3");
        r = p.calculate();
        double correct = 6.0;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

    @Test
    public void testExpression2() throws Exception {
        double r;

        Expression p = new Expression("1+2*3+4*5+6");
        r = p.calculate();
        double correct = 33.0;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

    @Test
    public void testExpression3() throws Exception {
        double r;

        Expression p = new Expression("1 - 7 / 7 + (2*(2*(2)))");
        r = p.calculate();
        double correct = 8;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

    @Test
    public void testExpression4() throws Exception {
        double r;

        Expression p = new Expression("7+(((((7-1))))-1)");
        r = p.calculate();
        double correct = 12.0;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

    @Test
    public void testExpression5() throws Exception {
        double r;

        Expression p = new Expression(" 20 + 15 - 10 * 29");
        r = p.calculate();
        double correct = -255.0;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

    @Test
    public void testExpression6() throws Exception {
        double r;

        Expression p = new Expression("-1+9");
        r = p.calculate();
        double correct = 8;
        if (r != correct) throw new Exception("EXPRESSION FAILED: " + r + " | " + correct);

    }

}

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 */
public class Expression {
    private MyTree<String> tree = new MyTree<>(2);
    private Queue<String> tokens = new LinkedList<>();
    private LinkedList<MyTree<String>> stack = new LinkedList<>();
    private Double oldCalculated = null;

    public Expression(String exp) {
        // 1 phase: parse tokens
        // -- (or more) or ++ (or more) are tokens -> print error if given
        // replace all unary and binary '-' with  '-1 *' and '+ -1 *' respectively
        Pattern p = Pattern.compile("((?:[\\-\\+]{2,})|(?:^[\\-\\+])?\\d+(?:\\.\\d+)?|[()\\-\\+\\*/])");
        Matcher m = p.matcher(exp);
        while (m.find()) {
            String token = m.group();
            if (token.equals("-") && isNumber(tokens.peek())) {
                tokens.add("+");
                tokens.add("-1");
                tokens.add("*");
            } else if (token.equals("-")) {
                tokens.add("-1");
                tokens.add("*");

            } else {
                tokens.add(token);
            }
        }

        for (String t : tokens) {
            System.out.print(t + " ");
        }
        System.out.println();

        // 2 phase: build binary tree
        parse();

    }

    private boolean isNumber(String s) {
        try {
            Double d = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;

        }
    }

    private void E() {
        T();
        Etail();
    }

    private void T() {
        F();
        Ttail();
    }

    private void F() {
        String token = peekToken();
        if (isNumber(token)) {
            MyTree<String> t = new MyTree<>(2);
            t.set(0, token);
            stack.push(t);
            popToken();
        } else {
            if (token.equals("(")) {
                match("(");
                E();
                match(")");
            }

            if (isNumber(token)) E();
            return;
        }

    }

    private void Etail() {
        String token = peekToken();
        if (token == null) return;
        switch (token) {
            case "+":
            case "-":
                match(token);

                MyTree<String> t = new MyTree<>(2);
                t.set(0, token);
                stack.push(t);

                T();
                Etail();
                makeTree();

                break;
            default:
                return;
        }
    }

    private void Ttail() {
        String token = peekToken();
        if (token == null) return;
        switch (token) {
            case "*":
            case "/":
                match(token);

                MyTree<String> t = new MyTree<>(2);
                t.set(0, token);
                stack.push(t);

                F();
                Ttail();
                makeTree();

                break;
            default:
                return;
        }
    }

    private String popToken() {
        return tokens.remove();
    }

    private String peekToken() {
        return tokens.peek();
    }

    private void match(String expected) {
        if (peekToken() == null) return;
        if (!peekToken().equals(expected)) {
            throw new IllegalStateException("Error at: " + peekToken());
        } else popToken();
    }

    private void makeTree() {
        if (stack.size() == 1) return;
        MyTree<String> left = stack.pop();
        MyTree<String> root = stack.pop();
        MyTree<String> right = stack.pop();

        root.hang(left, 2, 0);
        root.hang(right, 1, 0);

        stack.push(root);
    }

    private void parse() {
        E();
        match("");
        while (stack.size() != 1) makeTree();
        this.tree = stack.isEmpty() ? null : stack.pop();
    }

    private Double getNumber(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @return calculated value
     */
    public double calculate() {
        if (oldCalculated == null) {
            tree.postorderTraversal(0, (p, v) -> {
                Integer position = (Integer) p;
                String value = (String) v;
                Double n = 0.0;

                String a = tree.get(tree.jthChild(position, 0));
                String b = tree.get(tree.jthChild(position, 1));

                // is it leaf?
                if (a == null || b == null) return null;

                Double left = getNumber(a);
                Double right = getNumber(b);

                switch (value) {
                    case "*":
                        n = left * right;
                        break;
                    case "+":
                        n = left + right;
                        break;
                    case "-":
                        n = left - right;
                        break;
                    case "/":
                        n = left / right;
                        break;
                    default:
                        return null;
                }
                tree.set(tree.jthChild(position, 0), null);
                tree.set(tree.jthChild(position, 1), null);
                tree.set(position, String.valueOf(n));
                return null;
            });

            return getNumber(tree.get(0));
        } else return oldCalculated;
    }

    public static void main(String[] a) {
        // it accepts floating point and integer numbers, +-*/()
        Expression p = new Expression(" 20 + 15 - 10 * 29");

        System.out.println("Answer is " + p.calculate());

    }
}

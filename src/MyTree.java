import java.util.function.BiFunction;

/**
 * @author Bogdan Vaneev
 *         Innopolis University
 */
public class MyTree<E> {
    /**
     * Number of elements in the tree
     */
    private int size = 0;

    /**
     * Current tree height
     */
    private int height = 1;
    /**
     * Size of last (vacant) level
     */
    private int lastLevelSize;
    /**
     * Maximum number of children in the tree.
     */
    private int degree;
    private E[] tree;

    /**
     * Resize current array-based tree to te new_capacity
     *
     * @param new_capacity - positive number
     */
    @SuppressWarnings("unchecked")
    private void resize(int new_capacity) {
        if (new_capacity <= 0) throw new IllegalArgumentException("new_capacity must be > 0");
        int size = new_capacity < tree.length ? new_capacity : tree.length;
        E[] t = (E[]) new Object[new_capacity];
        if (tree != null) {
            // deep copy
            System.arraycopy(tree, 0, t, 0, size);
        }
        // I hope garbage collector will delete old memory
        tree = t;
    }

    private void addLevel() {
        // sum of the 'height' elements of geometrical progression
        // q = degree, 1 = first element of progression, lastLevelSize = last element of progression
        int new_size = (1 - lastLevelSize * degree) / (1 - degree);
        resize(new_size);

        lastLevelSize = (int) Math.pow(degree, ++height);
    }

    public MyTree(int degree) {
        this.degree = degree;
        this.tree = (E[]) new Object[1];
        this.lastLevelSize = (int) Math.pow(degree, height);
    }

    /**
     * Hang subtree with root with index p of tree T to this tree in the i-th index
     *
     * @param T - tree to hang
     * @param i - index, where we want to hang tree T (root of T will be on that index)
     * @param p - index of element which we want to hang to this tree
     */
    public void hang(MyTree<E> T, int i, int p) {
        set(i, T.get(p));
        for (int j = 0; j < degree; j++) {
            if (T.get(jthChild(p, j)) != null) {
                hang(T, jthChild(i, j), jthChild(p, j));
            }
        }
    }

    /**
     * Returns index of the parent of the i-th node
     *
     * @param i - index of the node
     * @return index
     */
    public int parent(int i) {
        return (i - 1) / degree;
    }

    /**
     * Returns index in the tree of the j-th child of the i-th node
     *
     * @param i - index of parent
     * @param j - index of the parent's child
     * @return index
     */
    public int jthChild(int i, int j) {
        return degree * i + j + 1;
    }

    /**
     * Getter for elements of the tree
     *
     * @param i - index of the node
     * @return
     */
    public E get(int i) {
        if (i < 0 || i >= tree.length) {
            if (i < tree.length + lastLevelSize) return null;
            else throw new IndexOutOfBoundsException("Out Of Bounds in get(" + i + ");");
        } else return tree[i];
    }

    /**
     * Setter for tree elements
     *
     * @param i    - index of the node
     * @param data - data to save
     * @return - returns old value
     */
    public E set(int i, E data) {
        if (i < 0 || i >= tree.length) {
            if (i >= tree.length && i < tree.length + lastLevelSize) {
                addLevel();
            } else {
                throw new IndexOutOfBoundsException("Out Of Bounds in set(" + i + ", " + data + ");");
            }
        }

        // you can't add node without parent if it is not root!
        if (i != 0 && tree[parent(i)] == null) throw new IndexOutOfBoundsException("Parent of i-th node is null. ");

        // previous value
        E temp = tree[i];

        if (temp == null && data != null) size++;        // addition
        else if (temp != null && data == null) size--;   // deletion

        tree[i] = data;
        return temp;
    }

    /**
     * Number of elements in the tree.
     *
     * @return
     */
    public int size() {
        return this.size;
    }

    /**
     * True if tree consists of 0 elements.
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Preorder traversal algorithm
     * @param i      - start index
     * @param action - BiFunction with 1st argument -- current position, 2nd argument -- value of the tree in this position
     */
    public void preorderTraversal(int i, BiFunction action) {
        // perform visit
        // System.out.println("tree[" + i + "] \t=\t" + tree[i]);
        action.apply(i, tree[i]);

        for (int j = 0; j < degree; j++) {
            int index = jthChild(i, j);
            if (get(index) != null) {
                preorderTraversal(index, action);
            }
        }
    }

    /**
     * Postorder traversal algorithm
     * @param i      - start index
     * @param action - BiFunction with 1st argument -- current position, 2nd argument -- value of the tree in this position
     */
    public void postorderTraversal(int i, BiFunction action) {
        for (int j = 0; j < degree; j++) {
            int index = jthChild(i, j);
            if (get(index) != null) {
                postorderTraversal(index, action);
            }
        }
        // perform visit
        //System.out.println("tree[" + i + "] \t=\t" + tree[i]);

        action.apply(i, tree[i]);
    }

    /**
     * Prints neat on-side tree scheme
     */
    public void print() {
        showBinTree(0, 0);
    }

    private void showBinTree(int index, int depth) {
        if (this.get(this.jthChild(index, 0)) != null) {
            showBinTree(this.jthChild(index, 0), depth + 1);
        }

        for (int i = 0; i < depth; i++) {
            System.out.print("   ");
        }
        System.out.println(this.get(index));

        if (this.get(this.jthChild(index, 1)) != null) {
            showBinTree(this.jthChild(index, 1), depth + 1);
        }
    }

    public static void main(String[] arg) {
        MyTree<Integer> tree1 = new MyTree<>(2);
        for (int i = 0; i < 5; i++) {
            tree1.set(i, i + 1);
        }

        MyTree<Integer> tree2 = new MyTree<>(2);
        for (int i = 0; i < 3; i++) {
            tree2.set(i, -i);
        }

        tree1.hang(tree2, 5, 0);

        tree1.print();

        System.out.println();

        // 2nd argument is a lambda BiFunction(int position, E value){}
        tree1.postorderTraversal(0, (position, value) -> {
            System.out.println("[" + position + "]=" + value);
            return null;
        });

    }

}

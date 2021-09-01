
import java.security.InvalidParameterException;
import java.util.LinkedList;
import static java.lang.Math.*;


/**
 * a fibonacci heap holds nodes in trees such that each tree keeps the min heap property and each tree has at least
 * golden_ratio^degree nodes.
 * @param <T>
 */
public class FibonacciHeap<T> extends Heap<T> {
    public static final double GOLDEN_RATIO =1.61803398875;

    /**
     * constructor.
     */
    FibonacciHeap(){
        root = null;
        size = 0;
    }

    /**
     * private constructor for internal use.
     * @param root set the root of the heap on initialization.
     */
    private FibonacciHeap(Node<T> root){
        this.root = root;
        size = 0;
    }

    /**
     * insert new node into the heap.
     * @param node the node to be inserted.
     */
    @Override
    void insert(Node<T> node) {
        if( node != null){
            size++;
            node.setChild(null);
            node.setParent(null);
//            if the heap is empty set node as root.
            if (root == null){
                root = node;
                node.setLeft_sibling(node);
                node.setRight_sibling(node);
            }else { // add node as a new tree to heap
                node.setLeft_sibling(root.getLeft_sibling());
                node.setRight_sibling(root);
                root.getLeft_sibling().setRight_sibling(node);
                root.setLeft_sibling(node);
                if (node.getKey() < root.getKey()){
                    root = node;
                }
            }
        }
    }

    /**
     *
     * @return the node with minimal key in heap.
     */
    @Override
    Node<T> minimum() {
        return root; // root always has minimal key in heap.
    }

    /**
     * remove the node with minimal key in heap from the heap.
     * @return the node with minimal key in heap.
     */
    @Override
    Node<T> extractMin() {
        Node<T> min_node = root;
        if (root == null){
            return null;
        }
        if (root.getLeft_sibling() == root){ // if there is only one tree in the heap.
            root = toHeap(root.getChild()); //  make all of the root's children trees.
        }else{
//              remove root from heap and add it's children as trees.
            Node<T> newroot = toHeap(root.getChild());
            FibonacciHeap<T> H = new FibonacciHeap<>(newroot);
            root.getLeft_sibling().setRight_sibling(root.getRight_sibling());
            root.getRight_sibling().setLeft_sibling(root.getLeft_sibling());
            root = toHeap(root.getRight_sibling());
            union(H);
        }
//          consolidate the trees.
        consolidate();
        min_node.setLeft_sibling(null);
        min_node.setRight_sibling(null);
        min_node.setChild(null);
        size--;
        return min_node;
    }

    /**
     * find the node with minimal key and set all their parents to null.
     * @param child a node in the linked-list.
     * @return the minimal node.
     */
    private Node<T> toHeap(Node<T> child) {
        if (child == null){
            return null;
        }
        Node<T> y = child.getRight_sibling();
        Node<T> min = child;
        do {
            if (y.getKey() < min.getKey()){
                min = y;
            }
            y = y.getRight_sibling();
            y.setParent(null);
        }while(y != child);
        return min;
    }

    /**
     * consolidate the trees in the heap until there is at most one of each degree
     */
    private void consolidate(){
//        initilize an empty array of nodes of size of max tree
        Node<T>[] A = new Node[(int) (log(size)/log(GOLDEN_RATIO)) + 1];
        Node<T> y = root;
        Node<T> x = y;
//        try to add each tree to the array at the index of it's degree. if its already full link them and try again.
        do{
            int deg = x.getDegree();
            if (A[deg] == null){
                A[deg] = x;
                if (y == x){
                    y = x.getRight_sibling();
                }
                x = y;
            }else{
                if (A[deg].getKey() < x.getKey()){
                    if (y == x){
                        y = x.getRight_sibling();
                    }
                    x.getRight_sibling().setLeft_sibling(x.getLeft_sibling());
                    x.getLeft_sibling().setRight_sibling(x.getRight_sibling());
                    A[deg].fibonacciLink(x);
                    x = A[deg];
                }else {
                    A[deg].getRight_sibling().setLeft_sibling(A[deg].getLeft_sibling());
                    A[deg].getLeft_sibling().setRight_sibling(A[deg].getRight_sibling());
                    x.fibonacciLink(A[deg]);
                }
                A[deg] = null;
            }
        }while(y != root);
    }

    /**
     * combine two heaps together.
     * @param H the heap to combine with.
     */
    @Override
    void union(Heap<T> H) {
        if (H == null || H.root == null){
            return;
        }
        if (root == null){
            root = H.root;
            size = H.size;
            return;
        }
        Node<T> right = root.getRight_sibling();
        Node<T> left = H.root.getRight_sibling();
        root.setRight_sibling(left);
        left.setLeft_sibling(root);
        H.root.setRight_sibling(right);
        right.setLeft_sibling(H.root);
        if (H.root.getKey() < root.getKey()){
            root = H.root;
        }
        size += H.size;
        H.size = 0;
        H.root = null;
    }

    /**
     * decrease the key of a given node in the heap to a given value.
     * @param node the node whose key we are decreasing.
     * @param key the new value of the key, must be lower than current value and non-negative.
     * @return the node.
     */
    @Override
    Node<T> decreaseKey(Node<T> node, int key) {
        if (key > node.getKey() || key < 0 ){
            throw new InvalidParameterException("New key must be less than existing key and non-negative");
        }
        return internalDecrease(node,key);
    }

    /**
     * private internal method to decrease the key of a given node in the heap to a given value.
     * @param node the node whose key we are decreasing.
     * @param key the new value of the key.
     * @return the node.
     */
    private Node<T> internalDecrease(Node<T> node, int key){
        node.setKey(key);
        cascadingCut(node);
        return node;
    }

    /**
     * cut the node from the tree. Then go up the tree if the node is marked cut it and go up. else mark it and end.
     * @param node the node to cut.
     */
    private void cascadingCut(Node<T> node) {
        if (node.getParent() != null && node.getKey() > node.getParent().getKey()){
            Node<T> cascade = node.getParent();
            cut(node);
            while (cascade != null && cascade.getMark()){
                Node<T> parent = cascade.getParent();
                parent.setDegree(parent.getDegree() - 1);
                cut(cascade);
                cascade = parent;
            }
            if (cascade != null){
                cascade.setMark(true);
            }
        }else if (node.getParent() == null && node.getKey() < root.getKey()){
            root = node;
        }
    }

    /**
     * cut the node from the tree and add it to the heap as a new tree.
     * @param node to cut.
     */
    private void cut(Node<T> node) {
        if (node.getParent() != null || node.getParent().getChild() == node){
            if (node.getRight_sibling() == node){
                node.getParent().setChild(null);
            }else{
                node.getParent().setChild(node.getRight_sibling());
                node.getRight_sibling().setLeft_sibling(node.getLeft_sibling());
                node.getLeft_sibling().setRight_sibling(node.getRight_sibling());
            }
        }
        node.setRight_sibling(node);
        node.setLeft_sibling(node);
        FibonacciHeap<T> H = new FibonacciHeap<T>(node);
        union(H);
    }

    /**
     * delete the given node from the heap
     * @param node to delete.
     * @return the node
     */
    @Override
    Node<T> delete(Node<T> node) {
        internalDecrease(node,-1);
        return extractMin();
    }

    void drawHeap(){
        LinkedList<Node<T>> current = new LinkedList<Node<T>>();
        LinkedList<Node<T>> next = new LinkedList<Node<T>>();
        String string;
        Node<T> x;
        Node<T> y = root;
        Node<T> child;
        do{
            current.addLast(y);
            while (!current.isEmpty()){
                x = current.removeFirst();
                child = x.getChild();
                if (child != null){
                    do {
                        next.addLast(child);
                        child = child.getRight_sibling();
                    } while (child != x.getChild());
                }
                int numberOfSpaces = (int) Math.pow(2, x.getDegree());
                if (numberOfSpaces == 1){
                    numberOfSpaces = 2;
                }
                string = String.format("%" + 3*numberOfSpaces + "d", x.getKey());
                System.out.print(string);
                if (current.isEmpty()){
                    System.out.println();
                    LinkedList<Node<T>> placeholder = current;
                    current = next;
                    next = placeholder;
                }
            }
            y = y.getRight_sibling();
            System.out.println();
        }while (y != root);
    }
}

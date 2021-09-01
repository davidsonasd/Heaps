import java.security.InvalidParameterException;

/**
 * a node class for nodes in binomial or fibonacci heaps.
 * @param <T> generic class
 */
public class Node <T> {

    private T data; // data held by the node
    private int key; // the key of the node
    private int degree; // number of children of the node
    private Node<T> parent; // parent of node
    private Node<T> right_sibling; // the node to the right of the node
    private Node<T> left_sibling; // the node to the left of the node
    private Node<T> child; // the child of the node, all other children accessed via this node
    private Boolean mark; // signifies in fibonacci heap if this node has lost a child.

    /**
     * constructor for node
     * @param data the data held by the new node
     * @param key the key of the new node
     */
    Node(T data, int key) {
        if (key < 0 ){
            throw new InvalidParameterException("Key must be non-negative");
        }
        this.data = data;
        this.key = key;
        degree = 0;
        mark = false;
    }

    /**
     * setter for child
     * @param child new child value
     */
    protected void setChild(Node<T> child) {
        this.child = child;
    }

    /**
     * setter for key
     * @param key new key value
     */
    protected void setKey(int key) {
        this.key = key;
    }

    /**
     * setter for parent
     * @param parent new parent value
     */
    protected void setParent(Node<T> parent) {
        this.parent = parent;
    }

    /**
     * setter for right_sibling
     * @param right_sibling new right_sibling value
     */
    protected void setRight_sibling(Node<T> right_sibling) {
        this.right_sibling = right_sibling;
    }

    /**
     * setter for left_sibling
     * @param left_sibling new left_sibling value
     */
    protected void setLeft_sibling(Node<T> left_sibling) {
        this.left_sibling = left_sibling;
    }

    /**
     * setter for data
     * @param data new data value
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * setter for mark
     * @param mark new mark value
     */
    protected void setMark(Boolean mark){
        this.mark = mark;
    }

    /**
     * setter for degree
     * @param degree new degree value
     */
    protected void setDegree(int degree) {
        this.degree = degree;
    }

    /**
     * getter for key
     * @return key value
     */
    protected int getKey() {
        return key;
    }

    /**
     * getter for child
     * @return child value
     */
    protected Node<T> getChild() {
        return child;
    }

    /**
     * getter for parent
     * @return parent value
     */
    protected Node<T> getParent() {
        return parent;
    }

    /**
     * getter for right_sibling
     * @return right_sibling value
     */
    protected Node<T> getRight_sibling() {
        return right_sibling;
    }

    /**
     * getter for left_sibling
     * @return left_sibling value
     */
    protected Node<T> getLeft_sibling() {
        return left_sibling;
    }

    /**
     * getter for data
     * @return data value
     */
    public T getData() {
        return data;
    }

    /**
     * getter for mark
     * @return mark value
     */
    protected Boolean getMark(){
        return mark;
    }

    /**
     * getter for degree
     * @return degree value
     */
    protected int getDegree() {
        return degree;
    }

    /**
     * link the roots of two trees in a binomial heap into one tree.
     * @param newChild
     */
    protected void binomialLink(Node<T> newChild){
        newChild.setParent(this);
        newChild.setRight_sibling(child);
        child = newChild;
        degree += 1;
    }

    /**
     * link the roots of two trees in a fibonacci heap into one tree.
     * @param newChild
     */
    protected void fibonacciLink(Node<T> newChild){
        if ( newChild == null){
            return;
        }
        newChild.setParent(this);
        if (child != null){
            newChild.setRight_sibling(child);
            newChild.setLeft_sibling(child.getLeft_sibling());
            child.getLeft_sibling().setRight_sibling(newChild);
            child.setLeft_sibling(newChild);
            child = newChild;
        }else {
            child = newChild;
            child.setRight_sibling(child);
            child.setLeft_sibling(child);
        }
        child.mark = false;
        degree++;
    }

}

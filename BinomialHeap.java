import java.security.InvalidParameterException;
import java.util.LinkedList;

/**
 * a binomial heap stores nodes in binomial trees each tree keeps the min heap property, with at most one tree of each
 * degree.
 * @param <T>
 */
public class BinomialHeap<T> extends Heap<T> {

    /**
     * constructor
     */
    BinomialHeap() {
        root = null;
        size = 0;
    }

    /**
     * insert new node into the heap.
     * @param node the node to be inserted.
     */
    @Override
    void insert(Node<T> node) {
        if (node == null){
            return;
        }
        size++;
        node.setRight_sibling(null);
        node.setParent(null);
        if (root == null){
            root = node;
            return;
        }
//        add node to a new heap and combine the two heaps.
        BinomialHeap<T> H = new BinomialHeap<>();
        H.root = node;
        union(H);
    }

    /**
     *
     * @return the node with minimal key in heap.
     */
    @Override
    Node<T> minimum() {
        if (root == null){
            return null;
        }
        Node<T> min = root;
        Node<T> x = root;
        while(x.getRight_sibling() != null){
            x = x.getRight_sibling();
            if (x.getKey() < min.getKey()){
                min = x;
            }
        }
        return min;
    }

    /**
     * remove the node with minimal key in heap from the heap.
     * @return the node with minimal key in heap.
     */
    @Override
    Node<T> extractMin() {
        Node<T> min = root;
        Node<T> x = root;
        BinomialHeap<T> H = new BinomialHeap<>();
        if (x == null){
            return null;
        }
//        find min.
        while (x.getRight_sibling() != null) {
            x = x.getRight_sibling();
            if (x.getKey() < min.getKey()) {
                min = x;
            }
        }
//        disconnect min from the heap.
        x = root;
        if (x == min){
            root = min.getRight_sibling();
        }else {
            while (x != null && x.getRight_sibling() != min) {
                x = x.getRight_sibling();
            }
            x.setRight_sibling(min.getRight_sibling());
        }
        Node<T> y = min.getChild();
        min.setRight_sibling(null);
        min.setChild(null);
//        add min's children to new list, then turn them into a new heap
        LinkedList<Node<T>> list = new LinkedList<Node<T>>();
        while (y != null){
            y.setParent(null);
            list.addFirst(y);
            y = y.getRight_sibling();
            list.getFirst().setRight_sibling(null);
        }
        if (!list.isEmpty()){
            y = list.removeFirst();
            H.root = y;
            while (!list.isEmpty()){
                y.setRight_sibling(list.removeFirst());
                y = y.getRight_sibling();
            }
        }
//        combine the two heaps.
        union(H);
        size--;
        return min;
    }

    /**
     * combine two heaps together.
     * @param H the heap to combine with.
     */
    @Override
    void union(Heap<T> H) {
        Node<T> x = root;
        Node<T> y = H.root;
        Node<T> z;
//        link all the trees from both heaps together in ascending order of degree.
        if (x != null && ( y == null ||  x.getDegree() <= y.getDegree() ) ){
            z = x;
            x = x.getRight_sibling();
        } else {
            z = y;
            y = y.getRight_sibling();
        }
        Node<T> last = z;
        while (x != null || y != null){
            if (x != null && ( y == null ||  x.getDegree() <= y.getDegree() ) ){
                z.setRight_sibling(x);
                z = z.getRight_sibling();
                x = x.getRight_sibling();
            } else {
                z.setRight_sibling(y);
                z = z.getRight_sibling();
                y = y.getRight_sibling();
            }
        }
        z = last;
        root = z;
//        link the trees of similar degree until there are at most only one tree of each degree.
        while (z.getRight_sibling() != null){
            if (z.getDegree() == z.getRight_sibling().getDegree()){
                if (z.getKey() < z.getRight_sibling().getKey()){
                    Node<T> p = z.getRight_sibling().getRight_sibling();
                    z.binomialLink(z.getRight_sibling());
                    z.setRight_sibling(p);
                }else {
                    if (last != z) {
                        last.setRight_sibling(z.getRight_sibling());
                        z.getRight_sibling().binomialLink(z);
                        z = last.getRight_sibling();
                    }else {
                        last = z.getRight_sibling();
                        z.getRight_sibling().binomialLink(z);
                        z = last;
                        root = z;
                    }
                }
            } else if (z.getDegree() > z.getRight_sibling().getDegree()){
                Node<T> placeholder = z.getRight_sibling();
                if(z != last){
                    last.setRight_sibling(placeholder);
                }else {
                    root = placeholder;
                }
                last = placeholder;
                z.setRight_sibling(last.getRight_sibling());
                last.setRight_sibling(z);
            }else {
                if (last == z){
                    root = last;
                }
                last = z;
                z = z.getRight_sibling();
            }
        }
        size += H.size;
        H.root = null;
        H.size = 0;
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
        return internalDecreaseKey(node,key);
    }

    /**
     * private internal method to decrease the key of a given node in the heap to a given value.
     * @param node the node whose key we are decreasing.
     * @param key the new value of the key.
     * @return the node.
     */
    Node<T> internalDecreaseKey(Node<T> node, int key) {
        node.setKey(key);
        T data = node.getData();
//          switch the node with its parent until it's parent's key is lower or until we reach the root of the tree.
        while (node.getParent() != null && node.getKey() < node.getParent().getKey()){
            node.setKey(node.getParent().getKey());
            node.setData(node.getParent().getData());
            node = node.getParent();
            node.setData(data);
            node.setKey(key);
        }
        return node;
    }

    /**
     * delete the given node from the heap
     * @param node to delete.
     * @return the node
     */
    @Override
    Node<T> delete(Node<T> node) {
        internalDecreaseKey(node,-1);
        return extractMin();
    }

    void drawHeap(){
        LinkedList<Node<T>> current = new LinkedList<Node<T>>();
        LinkedList<Node<T>> next = new LinkedList<Node<T>>();
        String string;
        Node<T> x;
        Node<T> y = root;
        Node<T> child;
        while (y != null){
            current.addLast(y);
            while (!current.isEmpty()){
                x = current.removeFirst();
                child = x.getChild();
                while (child != null){
                    next.addLast(child);
                    child = child.getRight_sibling();
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
        }
    }
}

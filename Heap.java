import java.util.LinkedList;

/**
 * class holding nodes in trees.
 * @param <T>
 */
public abstract class Heap <T>{

    protected Node<T> root; //

    protected int size;

    /**
     *
     * @return the size of heap
     */
    public int getSize(){
        return size;
    }

    /**
     * insert new node into the heap.
     * @param node the node to be inserted.
     */
    abstract void insert(Node<T> node);

    /**
     *
     * @return the node with minimal key in heap.
     */
    abstract Node<T>  minimum();

    /**
     * remove the node with minimal key in heap from the heap.
     * @return the node with minimal key in heap.
     */
    abstract Node<T> extractMin();

    /**
     * combine two heaps together.
     * @param H the heap to combine with.
     */
    abstract void union(Heap<T> H);

    /**
     * decrease the key of a given node in the heap to a given value.
     * @param node the node whose key we are decreasing.
     * @param key the new value of the key, must be lower than current value and non-negative.
     * @return the node.
     */
    abstract Node<T> decreaseKey(Node<T> node, int key);

    /**
     * delete the given node from the heap
     * @param node to delete.
     * @return the node
     */
    abstract Node<T> delete(Node<T> node);

    abstract void drawHeap();
}

import java.util.Stack;

/**
 * A class which represents a binomial heap.
 * @author nikhi
 *
 */
public class BinomialHeap {
	
	/**
	 * A class which represents a node in the binomial heap --> a node of a binomial tree.
	 * @author nikhi
	 *
	 */
	class Node{
		int key;
		int degree;
		Node child;
		Node parent;
		Node sibling;
		
		
		public Node(int key) {
			this.key = key;
			this.degree = 0;
			this.child = null;
			this.sibling = null;
			this.parent = null;
		}
	}
	
	//Store the roots of various degree
	Node root;
	int size;
	Node minimum;
	
	/**
	 * A constructor for the binomial heap. Nodes holds the reference to the first node. Order maintained from left to right.
	 */
	public BinomialHeap() {
		this.root = null;
		this.size = 0;
	}
	
	/**
	 * Make a new instance of a empty heap.
	 * @return A empty heap.
	 */
	public BinomialHeap makeHeap() {
		return new BinomialHeap();
	}
	
	public void Insert(int key) {
		Node newNode = new Node(key);
		if(minimum == null || key < minimum.key)
			minimum = newNode;
		
		if(this.root == null) {
			this.root = newNode;
		}
		else {
			newNode.sibling = root;
			root = newNode;
			handleConflicts();
		}
	}
	
	/**
	 * Find the node with the minimum key.
	 * @return The node with the minimum value .
	 */
	public Node minimum() {
		return this.minimum;
	}
	
	/**
	 * Merge 2 nodes of the same degree.
	 * @param one The first tree.
	 * @param two The second tree.
	 * @return The node of the new tree with nodes merged.
	 */
	private void mergeNodes(Node one, Node two) {		
		Node temp = one.child;
		two.sibling = temp;
		one.child = two;
		two.parent = one;
		one.degree++;
	}
	
	/**
	 * Merge two heaps.
	 * @param toMerge The heap to merge to.
	 */
	public void Union(BinomialHeap toMerge) {
		if(this.root == null)
			this.root = toMerge.root;
		this.size += toMerge.size;
		
		Node head = this.root;
		Node otherHead = toMerge.root;
		//All operations for merge done here
		Node newHead = null;
		
		if(head.degree < otherHead.degree) {
			newHead = head;
			//move the pointer to the next sibling for comparisons.
			head = head.sibling;
		}
		else {
			newHead = otherHead;
			otherHead = otherHead.sibling;
		}
		this.root = newHead;
		//Go through all the roots and decide the order.
		while(head != null && otherHead!=null) {
			if(head.degree < otherHead.degree) {
				newHead.sibling = head;
				head = head.sibling;
			}
			else {
				newHead.sibling = otherHead;
				otherHead = otherHead.sibling;
			}
			newHead = newHead.sibling;
		}
		
		newHead.sibling = otherHead == null ? head : otherHead;
		handleConflicts();
	}
	
	
	/**
	 * Method to extract the smallest node in the binomial heap
	 * @return The node with the smallest value in the binomial heap.
	 */
	public Node extractMin() {
		//As the minimum node will always be the root add the siblings to the main heap
		Node current = this.root;
		BinomialHeap temp = new BinomialHeap();
		Node previous = null;
		
		if(root == this.minimum) {
			this.root = root.sibling;
		}
		else {
			while(current.sibling != minimum)
				current = current.sibling;
			
			previous = current;
			current = minimum.sibling;
			previous.sibling = current;
		}
		
		//This stack will hold the values in the decreasing order of degree. As degree goes from high->low from left to right.
		Stack<Node> store = new Stack<>();
		Node child = minimum.child;
		
		//Remove the minimum from the list of nodes, and delete all the satellite data it contains.
		Node minimumToReturn = minimum;
		minimumToReturn.child = null;
		minimumToReturn.sibling = null;
		
		this.minimum = null;
		
		//Pushing to stack as the children are arranged from increasing to decreasing degrees.
		while(child!=null) {
			store.push(child);
			//As we have removed the parent, remove the reference to the deleted parent.
			child.parent =null;
			child = child.sibling;
		}
		
		temp.root = store.peek();
		Node t = null;
		while(!store.isEmpty()) {
			t = store.pop();
			if(!store.isEmpty())
				t.sibling = store.peek();
		}
		t.sibling = null;
		Union(temp);
		
		//find the new minimum as the old one is deleted --> takes O(log n) --> but only during extract min.
		//findMinimum();
		
		return minimumToReturn;
	}
	
	
	/**
	 * The public facing method of the decrease key method. Finds the node and passes to the utility function.
	 * @param value The value to change to.
	 * @param nodeToChange The node to change.
	 */
	public void decreaseKey(int nodeToChange,int value) {
		Node nodeToDecrease = findNode(root, nodeToChange);
		if(nodeToDecrease == null)
		{
			System.out.println("Sorry the node doesn't exist in the heap");
			return;
		}
		
		decreaseKeyUtil(nodeToDecrease, value);
		
	}
	
	/**
	 * A method to delete a key from a binomial heap.
	 * @param key The key to delete.
	 */
	public void deleteKey(int key) {
		Node nodeToDelete = findNode(root, key);
		
		if(nodeToDelete == null) {
			System.out.println("We cannot remove nodes that are not present!!");
			return;
		}
		
		decreaseKeyUtil(nodeToDelete, Integer.MIN_VALUE);
		extractMin();
		
		System.out.println("The key has been deleted!!");
	}
	
	
	/**
	 * Method to find node of a value. Worst case might take O(n) as there are n nodes
	 * @param value The value to look for.
	 * @return Node if found else null.
	 */
	public Node findNode(Node node,int value) {
		if(node == null)
			return null;
		
		if(node.key == value)
			return node;
		
		Node current = node;
		Node temp = null;
		while(current != null) {
			//If current is the node return.
			if(current.key == value)
				return current;
			if(current.key < value)
				temp = findNode(current.child, value);
			if(temp != null)
				return temp;
			current = current.sibling;
		}
		return null;
	}
	
	
	/**
	 * A utility method to decrease the key of a node. Will only move to the root in the worst case(As all are individual trees).
	 * @param node The node to decrease key of.
	 * @param val The value to change to.
	 */
	private void decreaseKeyUtil(Node node, int val) {
		if(node == null)
			return;
		
		Node curr = node;
		curr.key = val;
		
		while(curr.parent != null) {
			if(curr.key < curr.parent.key) {
				int parentKey = curr.parent.key;
				curr.parent.key = curr.key;
				curr.key = parentKey;
				curr = curr.parent;
			}
			else
				break;
		}
		//If after decreasing this is the smallest key.
		if(val < minimum.key)
			this.minimum = curr;
		
		System.out.println("Key has been decreased to " + val);
	}
	
	
	/**
	 * Method to find new minimum after deletion.
	 */
	private void findMinimum() {
		//Not using this as the same thing can be done in handle conflicts!
		Node head = root;
		
		Node min = null;
		while(head!= null) {
			if(min == null) {
				min = head;
			}
			if(head.key < min.key) {
				this.minimum = min; 
			}
			head = head.sibling;
		}
	}
	
	
	public void printHeap() {
		Node curr = this.root;
		while(curr!=null) {
			printHeapUtil(curr);
			curr = curr.sibling;
			System.out.println();
		}
	}
	
	/**
	 * A method to print the heap and its contents.
	 */
	private void printHeapUtil(Node node) {
		if(node == null)
			return;
		System.out.print(node.key + " degree : " + node.degree + " " + "Parent : " + (node.parent!=null ? node.parent.key : "-1") + " ");
		Node child = node.child;
		while (child != null) {
			printHeapUtil(child);
			child = child.sibling;
		}
	}
	
	
	/**
	 * A private method to handle conflicts between nodes of the tree.
	 */
	private void handleConflicts() {
		Node current = this.root;
		Node next = current.sibling;
		Node prev = null;
		
		while(next!= null) {
			if(minimum == null || current.key < minimum.key) {
				minimum = current;
			}
			//next can also be the minimum.
			if(next.key < minimum.key) {
				minimum = next;
			}
			if(current.degree != next.degree || next.sibling!=null && current.degree == next.sibling.degree) {
				prev = current;
				current = next;
			}
			else if(current.key < next.key) {
				current.sibling = next.sibling;
				mergeNodes(current, next);
			}
			else {
				//no conflict encountered yet
				if(prev == null)
					this.root = next;
				else
					prev.sibling = next;
				mergeNodes(next, current);
				current = next;
			}
			next = current.sibling;
		}
	}
	
	
	public static void main(String [] args) {
		BinomialHeap heap = new BinomialHeap();
		heap.Insert(12);
		heap.Insert(21);
		
		BinomialHeap heap2 = new BinomialHeap();
		heap2.Insert(7);
		heap2.Insert(3);
		heap2.Insert(11);
		
		//Union demo
		heap.Union(heap2);
		
		System.out.println("After Union!");
		heap.printHeap();
		
		System.out.println("The minimum node extracted is " + heap.extractMin().key);
		heap.Insert(4);
		
		//Decrease key
		heap.decreaseKey(11, 1);
		heap.deleteKey(21);
		
		System.out.println("After extract min, deletion and decreasing the key");
		heap.printHeap();
		
		//check after inserting if the order is mantained or not
		heap.Insert(8);
		
		System.out.println("After inserting!");
		heap.printHeap();
	}
	
}

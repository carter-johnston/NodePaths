import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * @author Carter_Johnston
 * 
 * In order for the program to run as intended. A file named "tree.txt" must be present in 
 * the same directory the program is running in.
 * 
 * assign3.java was developed in eclipse, so if this is put in a project folder, make sure tree.txt is located outside of the source folder.
 * 
 * the file is read line by line. each line will contain integers separated by a space. 
 * the first integer in the line is intended to be the parent node with the following integers in a line being its child nodes.
 * Each integer represents a node's ID and must be unique and non-null.
 * 
 * Example of "tree.txt":
 * 
 * 1 2 3 4
 * 3 6 
 * 4 7 8
 * 
 * '1' is the root node. '2,3,4' are children of the root.
 * '3' exists as a child of the root. '6' is a child if '3'.
 * 
 * visual:
 * 
 *       ___1___
 *      |   |   |
 *      2   3  _4_
 *          | |   |
 *          6 7   8
 *          
 * The program will immediately traverse the tree after automatically reading and inserting the intended data.
 * A variant of preorder traversal (root,left,right) will be displayed. With use of a n-ary tree instead of a binary tree,
 * the program will print out a parent's child nodes from left to right.
 * 
 * The second part of the program will search the tree for all reachable nodes. It uses a depth-first search with a battery value for how many nodes it can traverse before needing to travel back
 * to the root node. For example, if the battery is '4', the program can traverse two nodes down before having to return to the root.
 * 
 * The user will be prompted to input a value for 'B' (battery). The value must be a single integer.
 * 
 * the path displayed shows the path needed to find all reachable nodes in the tree.
 * 
 * A 'Percentage of Exploration' is displayed showing the percent of reachable nodes to the total nodes in the tree.
 */
public class assign3<E> {
	
	static boolean flag = true;
	static Tree<Integer> tree;
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> set = fileRead("tree.txt");//reads from a text file "tree.txt".
		for(String temp:set) {//for each line, integers are split up and inserted into a tree structure.
			String[] tokens = temp.split(" ");
			for(int i = 1;i<tokens.length;++i) {
				if (flag == true){//will run once to instantiate the tree structure with the first integer read as the root.
					tree = new Tree<>(Integer.parseInt(tokens[0]));
				}
				flag = false;
				tree.insert(Integer.parseInt(tokens[i]),Integer.parseInt(tokens[0]),tree.root);
			}			
		}
		
		System.out.print("1. Pre-order Traversal: ");
		tree.preorder(tree.root);
		System.out.println();
		System.out.println();
		System.out.println("2. tree.txt: ");
		System.out.println();
		visual(set);
		System.out.println();
		System.out.println("3. Modified depth-first search: ");
		System.out.println();
		System.out.print("Enter a value 'B' for the modified search: ");
		Scanner scan = new Scanner(System.in);
		int b = 0;
		try {
		b = scan.nextInt();
		}
		catch(Exception e) {
			System.out.println();
			System.out.println();
			System.out.println("Invalid input. Please enter an integer value.");
			scan.close();
			return;
		}
		scan.close();
		tree.dfs(tree.root,b);
	}
 	public static ArrayList<String> fileRead(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String data;
		ArrayList<String> set = new ArrayList<String>();
		while((data = br.readLine()) != null) {
			set.add(data);
		}
		br.close();
		return set;
	}
	public static void visual(ArrayList<String> set) {
		for(String temp:set) {
			System.out.println(temp);
		}
	}
	public static class Tree<E>{
		private Node root;
		private int jumps;
		private ArrayList<Node> allNodes;
		private boolean flag = true;
		//private ArrayList<Node> expected;
		
		
		public Tree(E element) {
			Node n = new Node(element);
			this.allNodes = new ArrayList<>();
			n.depth = 0;
			root = n;
			allNodes.add(n);
		}
		/**
		 * only use insert when a Tree object. requires an existing root to function.
		 * @param element
		 * @param parent
		 * @param position
		 */
		public void insert(E element,E parent,Node position) {
			if(position.getElement() == parent) {
				Node n = new Node(position,element);
				position.setChild(n);
				allNodes.add(n);
				n.setDepth(findDepth(n));
			}
			else {
				if(position.getChildren() != null) {
					for(Node temp:position.getChildren()){
						insert(element,parent,temp);
					}
				}
			}
		}
		/**
		 * modified pre-order traversal of a tree structure. will traverse the tree (parent to left-most child to right-most child).
		 * use only preorder(Node Position).
		 * @param position
		 */
		public void preorder(Node position) {
			//  System.out.print("("+position.depth+")");
			System.out.print(position.getElement()+ " ");
			if(position.getChildren() != null) {
				for(Node temp : position.getChildren()) {
					preorder(temp);
				}
			}
		}
		public void preorder(Node position,int depth) {
			System.out.print(position.getElement()+ " ");
			if(position.getChildren() != null) {
				for(Node temp : position.getChildren()) {
					preorder(temp,depth-1);
				}
			}
		}
		public ArrayList<Node> expectNodes(int depth) {//only used by dfs(~).
			ArrayList<Node> expect = new ArrayList<>();
			for(Node temp:allNodes) {
				if(temp.depth <= depth) {
					expect.add(temp);
					//System.out.print(temp.getElement());
				}
			}
			return expect;
		}
		/**
		 * used to find the depth of an node. start at findDepth(Node position) and call the node you want to find the depth of. method will recursively trace back to root.
		 * @param position
		 * @param counter
		 * @return counter
		 */
		public int findDepth(Node position,int counter) {
			if(position.getParent() == null) {
				return counter;
			}
			else {
				counter += 1;
				counter = findDepth(position.getParent(),counter);
				return counter;
			}
		}
		public int findDepth(Node position) {
			int counter = 0;
			if(position.getParent() == null) {
				return counter;
			}
			else {
				counter += 1;
				counter = findDepth(position.getParent(),counter);
				return counter;
			}
		}
		/**
		 * Depth-first Search method starts here. dfs(int, ArrayList<Node>, Node) is called recursively.
		 * @param position
		 * @param battery
		 */
		public void dfs(Node position,int battery) {
			int depth = battery / 2;//constant travel distance.
			double percent = 0.0;
			ArrayList<Node> expected = null;
			if(depth <= 0) {
				System.out.println();
				System.out.println("No elements traversed.");
				System.out.println("");
				System.out.print("Percentage of Exploration: 0.00%");
				return;
			}
			if(flag == true) {//runs only once to instantiate expected list.
				System.out.println();
				expected = expectNodes(depth);
				Double num = Double.valueOf(expected.size());
				Double denom = Double.valueOf(allNodes.size());
				percent = (num / denom)*100;	
			}flag = false;

			if(position.getParent()==null) {//if at root - replenishes jumps.
				jumps = depth;
			}
			if(expected.size() == 1) {//tree fully traversed - happens only when depth == 0
				System.out.print(position.getElement());
				System.out.println("");
				System.out.println("");
				System.out.printf("Percentage of Exploration: %.2f",(percent));
				System.out.println("%");
				return;
			}
			System.out.print(position.getElement() + " -> ");//print node in path.
			//System.out.print("jumps:("+jumps+")");
			if(position.getChildren() == null || childrenVisited(expected,position.getChildren()) == true) {//if conditions are met, node will be marked as visited.
				expected.remove(position);
			}
			if(jumps > 0 && position.getChildren() != null && childrenVisited(expected,position.getChildren()) == false) {//traverse downward.
				for(Node temp:position.getChildren()) {
					if(expected.contains(temp)) {
						jumps -= 1;
						dfs(depth,expected,temp);
					}
				}
			}
			else {//traverse upward.
				jumps -= 1;
				dfs(depth,expected,position.getParent());
			}
			System.out.println("");
			System.out.println("");
			System.out.printf("Percentage of Exploration: %.2f",(percent));
			System.out.println("%");
		}
		public void dfs(int depth, ArrayList<Node> expected,Node position) {

			if(position.getParent()==null) {//if at root - replenishes jumps.
				jumps = depth;
			}
			if(expected.size() == 1) {//tree fully traversed.
				System.out.print(position.getElement());//print final node in path.
				return;
			}
			System.out.print(position.getElement() + " -> ");//print node in path.
			//System.out.print("jumps:("+jumps+")");//display jumps value.
			if(position.getChildren() == null || childrenVisited(expected,position.getChildren()) == true) {//if conditions are met, node will be marked as visited.
				expected.remove(position);
			}
			if(jumps > 0 && position.getChildren() != null && childrenVisited(expected,position.getChildren()) == false) {//traverse downward.
				for(Node temp:position.getChildren()) {
					if(expected.contains(temp)) {
						jumps -= 1;
						dfs(depth,expected,temp);
					}
				}
			}
			else {//traverse upward.
				jumps -= 1;
				dfs(depth,expected,position.getParent());
			}
		}
		
		public boolean childrenVisited(ArrayList<Node> expected,ArrayList<Node> children) {//used only in dfs(~)
			for(Node temp:children) {
				if(expected.contains(temp)) {
					return false;//false if a child is unvisited.
				}
			}
			return true;//true if all children are visited.
		}

		class Node{
			private E element = null;
			private Node parent = null;
			private ArrayList<Node> children = null;
			private int depth;
			
			public Node(){}

			public Node(E element) {
				this.element = element;
			}
			public Node(Node parent,E element) {
				this.parent = parent;
				this.element = element;
			}
			public int getDepth() {
				return depth;
			}
			public void setDepth(int depth) {
				this.depth = depth;
			}
			public E getElement() {
				return element;
			}
			public Node getParent(){
				return parent;
			}
			public ArrayList<Node> getChildren(){
				if(children == null) {
					return null;
				}
				else {
					return children;
				}
			}
			
			public void setParent(Node parent) {
				this.parent = parent;
			}
			
			public void setChild(Node child) {
				if(children == null) {
					children = new ArrayList<>();
				}
				this.children.add(child);
			}
		}		
	}
}

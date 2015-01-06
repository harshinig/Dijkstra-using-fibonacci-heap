
//Dijkstra's Shortest path algorithm using simple scheme and Fibonacci heap scheme

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

//class for vertices or nodes of the graph
class Node {                         
   int label;
   java.util.ArrayList<AdjacentNode> adjlist; //stores arraylist of neighbouring nodes
   public Node(int label, ArrayList<AdjacentNode> list) {
       this.label = label;
       this.adjlist = list;
   } 
}
//class for adjacent nodes to be stored in the adjacency list of each node
class AdjacentNode {
    int label;
    int weight;
    public AdjacentNode(int label, int weight) {
        this.label = label;
        this.weight = weight;   
    }    
}
//class for fibonacci node structure
class FibonacciHeapNode {
    int key,value,degree;
    FibonacciHeapNode parent;
    FibonacciHeapNode child,left,right;
    boolean cut;
    }
//main class
public class dijkstra {
    public static Node[] Nodes;
    public static boolean connected = false;
    static boolean[] visited;//for dfs
    static File fileInput;
    static int nodesCount;
    static int edgesCount;
    static int cost = 1000;
    static int sourceNode;
    static int[] distance;
    static FibonacciHeapNode root;   //pointer to minimum node
    static FibonacciHeapNode[] mintrees;

    public static void main(String[] args) throws FileNotFoundException {
        String mode = args[0];   
        if (mode.equals("-r")) {    //random mode
            String strNodesCount = args[1];
            String strDensity = args[2];
            String strSourceNode = args[3];
            nodesCount = Integer.parseInt(strNodesCount);
            float density = Float.parseFloat(strDensity);
            sourceNode = Integer.parseInt(strSourceNode);
            edgesCount = (int) (((density) * (nodesCount * (nodesCount - 1) / 2)) / 100);
            while (!connected) {    //to check if graph is connected
                GenerateRandomGraph(nodesCount, edgesCount);
            }
            GetShortestPath();
            PrintDistance(mode);
            GetShortestPathFibonacciHeap();
            PrintDistance(mode);
        } else {   //file input mode
            String filename = args[1];
            fileInput = new File(filename);
            FileInputGraph();
            if (mode.equals("-s")) {  //simple scheme
                GetShortestPath();
                PrintDistance(mode);
            } else {               //fibonacci heap scheme
                GetShortestPathFibonacciHeap();
                PrintDistance(mode);
            }
        }
    }

    public static void GetShortestPath() {    //function to display time taken for simple scheme
        System.out.println("Using Simple scheme");
        long start1 = System.currentTimeMillis();
        ShortestPath(nodesCount, sourceNode);
        long stop1 = System.currentTimeMillis();
         System.out.println("Time taken: "+(stop1 - start1));
    }

    public static void GetShortestPathFibonacciHeap() { //function to display time taken for Fibonacci heap scheme
        System.out.println("Using Fibonacci Heap");
        long start2 = System.currentTimeMillis();
        ShortestPathFibonacciHeap(nodesCount, sourceNode);
        long stop2 = System.currentTimeMillis();
        System.out.println("Time taken: "+(stop2 - start2));
    }

    public static void FileInputGraph() throws FileNotFoundException {  //to take input from a file
        try (Scanner scan = new Scanner(fileInput)) {
            sourceNode = scan.nextInt();
            nodesCount = scan.nextInt();
            int edgesno = scan.nextInt();

            Nodes = new Node[nodesCount];    //create an array equal to number of nodes
            for (int i = 0; i < nodesCount; i++) {
                Nodes[i] = new Node(i, new ArrayList<AdjacentNode>());
            }
            int count = 0;
            while (count < edgesno) {     //read all edges and add them to the adjacency lists
                int e1, e2, c;
                e1 = scan.nextInt();
                e2 = scan.nextInt();
                c = scan.nextInt();
                Nodes[e1].adjlist.add(new AdjacentNode(e2, c));
                Nodes[e2].adjlist.add(new AdjacentNode(e1, c));
                count++;
            }
        }
    }

    public static void GenerateRandomGraph(int n, int e) {  //to generate graph in random mode given number of nodes and edges
        int e1, e2,c;
        Nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            Nodes[i] = new Node(i, new ArrayList<AdjacentNode>());
        }
        int count = 0;
        Random gen1 = new Random();
        {
                 while(count<n){            //to generate edges for all nodes
                 e1=Nodes[count].label;
                 e2 = gen1.nextInt(n);
                    c= gen1.nextInt(cost) + 1; 
                    if (e1 !=e2 && !EdgeExists(e1, e2)) {
                 Nodes[e1].adjlist.add(new AdjacentNode(e2, c));
                Nodes[e2].adjlist.add(new AdjacentNode(e1, c)); 
             count ++;
             }
                    
             }
         while(count<e){                      //to generate remaining number of edges to complete the graph
             e1 = gen1.nextInt(n);
            e2= gen1.nextInt(n);
            c = gen1.nextInt(cost) + 1;
            if (e1 != e2 && !EdgeExists(e1, e2)) { 
                Nodes[e1].adjlist.add(new AdjacentNode(e2, c));
                Nodes[e2].adjlist.add(new AdjacentNode(e1, c));
             
                count++;
            }
         }
        }
     
        visited = new boolean[Nodes.length];  //to check if all nodes have been visited in the DFS algorithm
        DepthFirstSearch(Nodes[0]);     
        boolean flag = true;        
        for (int i = 0; i < Nodes.length; i++) {
            flag = flag && visited[i];
        }
        connected = flag;  //connected is set to true if none of the elements in visited[] is false
    }
    
    public static void DepthFirstSearch(Node thisNode) { //DFS using recursive function call to check if graph is connected
        visited[thisNode.label] = true;
        for (AdjacentNode node : thisNode.adjlist) {
            if (!visited[node.label]) {
                DepthFirstSearch(Nodes[node.label]);
            }
        }
    }

    public static boolean EdgeExists(int e1, int e2) { //to check if an edge is present in a graph
        boolean result = false;
        ArrayList<AdjacentNode> list = Nodes[e1].adjlist;
        for (AdjacentNode node : list) {
            if (node.label == e2) {
                result = true;
            }
        }
        return result;
    }

    public static void ShortestPath(int length, int source) {  //Shortest path using simple scheme
        distance = new int[length];
        int[] predecessor = new int[length];
        visited = new boolean[length];
        try {
            for (int k = 0; k < length; k++) {
                distance[k] = Integer.MAX_VALUE;
            }
            distance[source] = 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (int m = 0; m < length; m++) {
            int next = GetNextNode(distance, visited);   //function call to get the next minimum distance node
            visited[next] = true;
            //Get Adjacent List for the next minimum Node
            ArrayList<AdjacentNode> adjNodes = Nodes[next].adjlist;
            for (AdjacentNode thisNode : adjNodes) {            //for each neighbouring node of the minimum node,update distance values
                int newDist = distance[next] + thisNode.weight;
                if (distance[thisNode.label] > newDist) {
                    distance[thisNode.label] = newDist;
                    predecessor[thisNode.label] = next;
                }
            }
        }
        // for (int q = 0; q < predecessor.length; q++) {
        //   System.out.println("predecessor of " + q + ":  " + predecessor[q]);

        // }
    }

    public static int GetNextNode(int[] dist, boolean[] visited) {
        int minWeight = Integer.MAX_VALUE;
        int nextNode = -1;
        for (int p = 0; p < dist.length; p++) {
            if (!visited[p] && dist[p] < minWeight) {
                nextNode = p;
                minWeight = dist[p];
            }
        }
        return nextNode;
    }

    public static void ShortestPathFibonacciHeap(int length, int source) {  //Shortest path using fibonacci heap scheme
        distance = new int[length];
        visited = new boolean[Nodes.length];
        FibonacciHeapNode[] fnode = new FibonacciHeapNode[length];  //points to the fibonacci heap
        for (int k = 0; k < length; k++) {
            distance[k] = Integer.MAX_VALUE;
        }
        distance[source] = 0;

        for (int i = 0; i < Nodes.length; i++) {
            fnode[i] = Insert(distance[Nodes[i].label], Nodes[i].label);
        }
        while (root != null) {
            FibonacciHeapNode minNode = DeleteMin();   //to get next minimum node
                       visited[minNode.value] = true;
            distance[minNode.value] = minNode.key;
            fnode[minNode.value] = null;
            ArrayList<AdjacentNode> adjNodes = Nodes[minNode.value].adjlist;
            for (AdjacentNode thisNode : adjNodes) {   //update distances of neighbours using decreasekey function
                int newDist = minNode.key + thisNode.weight;
                if (fnode[thisNode.label] != null && newDist < fnode[thisNode.label].key) {
                    DecreaseKey(fnode[thisNode.label], newDist);
                }
            }
        }
    }

    public static void PrintDistance(String m) {   //to print shortest distance arrays
        if(m.equals("-s") ||m.equals("-f")){
        for (int i = 0; i < distance.length; i++) {
            System.out.println("The shortest distance from "+sourceNode+" to "+i+": "+distance[i] + "  ");
        }
        System.out.println();
    }
        else{
          for (int i = 0; i < distance.length; i++) {
            System.out.print(distance[i] + "  ");
        }  
           System.out.println();
        }
    
    }
 
//Fibonacci heap Functions

    public static void Add(FibonacciHeapNode f) {  //to add a fibonacci node to the heap
        f.parent = null;
        if (root == null) {
            root = f.left = f.right = f;
            return;
        }
        f.left = root.left;
        f.right = root;
        root.left.right = f;
        root.left = f;
        if (root.key > f.key) {
            root = f;
        }
    }

    public static FibonacciHeapNode Insert(int key, int value) {  //creates a new fibonacci heap node and calls the Add function
        FibonacciHeapNode temp = new FibonacciHeapNode();
        temp.value = value;
        temp.key = key;
        Add(temp);
        return temp;
    }

    public static FibonacciHeapNode DeleteMin() {         //Deletes the minimum element from the heap
        mintrees = new FibonacciHeapNode[Nodes.length];
        if (root == null) {
            return null;
        }
        FibonacciHeapNode rootNode = root;              
        FibonacciHeapNode present, previous;
        if ((present = root.child) != null) {
            do {
                previous = present;
                present = present.right;
                Join(previous);
            } while (present != root.child);
        }
        for (present = root.right; present != root;) {
            previous = present;
            present = present.right;
            Join(previous);
        }
        root = null;
        for (int i = 0; i < mintrees.length; i++) {
            if (mintrees[i] != null) {
                Add(mintrees[i]);
                mintrees[i] = null;
            }
        }
        return rootNode;  //returns new minimum element
    }

    public static void DecreaseKey(FibonacciHeapNode node, int newKey) {   //decreases key of a given fibonacci node
        FibonacciHeapNode parent = node.parent;
        node.key = newKey;
        if (parent == null) {
            if (root.key > node.key) {
                root = node;
            }
            return;
        }
        if (node.key < parent.key) {
            Remove(node);
            Add(node);
            for (node = parent, parent = node.parent; node.cut == true && parent != null; node = parent, parent = parent.parent) {  //cascading cut
                Remove(node);
                Add(node);
            }
            node.cut = true;
        }
    }

    public static void Remove(FibonacciHeapNode n) {   //removes an arbitrary node from the heap
        FibonacciHeapNode parent = n.parent;
        if (n.right == n) {
            parent.child = null;
        } else {
            n.left.right = n.right;
            n.right.left = n.left;
            if (parent.child == n) {
                parent.child = n.right;
            }
        }
    }

    public static void Join(FibonacciHeapNode node) {  //joins two trees by making one the child of another
        while (mintrees[node.degree] != null) {
            FibonacciHeapNode newRoot, newChild;
            if (mintrees[node.degree].key < node.key) {  //minTrees stores trees of different degrees upto max degree
                newRoot = mintrees[node.degree];
                newChild = node;
            } else {
                newRoot = node;
                newChild = mintrees[node.degree];
            }
            newChild.parent = newRoot;
            newChild.cut = false;
            if (newRoot.child == null) {
                newRoot.child = newChild.left = newChild.right = newChild;
            } else {
                newChild.left = newRoot.child.left;
                newChild.right = newRoot.child;
                newRoot.child.left.right = newChild;
                newRoot.child.left = newChild;
            }
            node = newRoot;
            mintrees[node.degree++] = null;
        }
        mintrees[node.degree] = node;
    }
}
// End of program
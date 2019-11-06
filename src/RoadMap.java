import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class RoadMap<T extends Comparable<? super T>> {
	private int size;
	public Hashtable<String, Node> referenceTable;
	public enum NodeType {CITY, LANDMARK, OTHER};
	public enum EdgeType {HIGHWAY, MAIN_ROAD, RURAL_ROAD, OTHER};
	
	public RoadMap() {
		size = 0;
		referenceTable = new Hashtable<String, Node>();
	}
	
	public int getSize() {
		return size;
	}
	
	public Node getNodeFromString(String key) throws NullPointerException {
		Node value = referenceTable.get(key);
		if(value == null) {
			System.out.println("City " + key + "does not exist! Are you sure you spelled it right?");
			throw new NullPointerException();
		}
		return value;
	}
	
	public double getStraightLineDistance(Node node1, Node node2) {
		if(node1 == null | node2 == null) throw new NullPointerException();

		double lat1 = (Math.PI/ 180) * (node1.latitude); // Convert to radians
		double lat2 = (Math.PI/ 180) * (node2.latitude);
		double long1 = (Math.PI/ 180) * (node1.longitude);
		double long2 = (Math.PI/ 180) * (node2.longitude);
		double a = Math.pow((Math.sin((lat2 - lat1)) / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((long2 - long1) / 2) , 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double R = 3959; // In miles
		
		return c*R;
	}
	
	public ArrayList<String> searchForCities(String input) {
		ArrayList<String> matches = new ArrayList<String>();
		
		for(String s: referenceTable.keySet()) if(s.contains(input)) matches.add(s);
		
		return matches;
	}
		
	public ArrayList<Node> findMinDistance(Node start, Node end) {
		
		PriorityQueue<ComparableNode> queue = new PriorityQueue<ComparableNode>(); 
//		ComparableNode first =  new ComparableNode(start, (int) Math.round(this.getStraightLineDistance(start, end)));
		ComparableNode first =  new ComparableNode(start, 0);
		ArrayList<Node> connected = start.getConnectedCities();
		for(int i = 0; i < connected.size(); i++) {
			
			queue.add(first.createNewBranch(connected.get(i), (int) Math.round(start.connectedRoads.get(i).distance)));
		}

		System.out.println("Initialized");
		System.out.println(queue);
		
//		System.out.println("head:");
//		System.out.println(queue.peek().distance);
		
		int index = 1;
		
		while(!queue.peek().currentPath.get(index).name.equals(end.name)) {
//			System.out.println(queue.peek().currentPath.get(index).name + " does not equal" + end.name);
			connected = queue.peek().currentPath.get(index).getConnectedCities();
			
			ComparableNode saveCompare = queue.poll();
			Node saveNode = saveCompare.currentPath.get(index);
			for(int i = 0; i < connected.size(); i++) {
				if(!saveCompare.currentPath.contains(connected.get(i))){
					ComparableNode addToPath = saveCompare.createNewBranch(connected.get(i), (int) Math.round(saveNode.connectedRoads.get(i).distance));
					queue.add(addToPath);
				}
			}
			System.out.println("After some paths:");
			System.out.println(queue);
			
			index = queue.peek().currentPath.size() - 1;
			
		}
		
		
		return queue.peek().currentPath;
	}
	
	public boolean addNode(String name, NodeType type, double latitude, double longitude) {
		referenceTable.put(name, new Node(name, type, latitude, longitude));
		return true;
	}
	
	public boolean addEdge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
		new Edge(firstNode, secondNode, name, type, distance);
		return true;
	}
	
	
	public class Node {
		private ArrayList<Edge> connectedRoads;
		
		private String name;
		private NodeType type;
		private double latitude;
		private double longitude;
		
		public Node(String name, NodeType type, double latitude, double longitude) {
			this.connectedRoads = new ArrayList<Edge>();
			
			this.name = name;
			this.type = type;
			this.latitude = latitude;
			this.longitude = longitude;
			size++;
		}
		
		public ArrayList<Edge> getConnectedRoads() {
			return connectedRoads;
		}
		
		public ArrayList<Node> getConnectedCities() {
			ArrayList<Node> nodes = new ArrayList<Node>();
			for(Edge edge : connectedRoads) nodes.add(edge.getOtherNode(this));
			return nodes;
		}
		
		public String toString() {
			return name;
		}
		
	}
	
	public class Edge {
		private Node firstNode;
		private Node secondNode;
		
		private String name;
		private EdgeType type;
		private double distance;
		
		public Edge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
			this.firstNode = firstNode;
			this.secondNode = secondNode;
			
			this.firstNode.connectedRoads.add(this);
			this.secondNode.connectedRoads.add(this);
			
			this.name = name;
			this.type = type;
			this.distance = distance;
		}
		
		private Node getOtherNode(Node node) {
			if(firstNode.equals(node)) {
				return secondNode;
			}
			return firstNode;
		}
		
		public String toString() {
			return name;
		}
	}
	
	private class ComparableNode implements Comparable<ComparableNode> {
		ArrayList<Node> currentPath;
		Integer distance;
		
		public ComparableNode(Node node, Integer distance) {
			this.currentPath = new ArrayList<Node>();
			currentPath.add(node);
			this.distance = distance;
		}

		public ComparableNode(ArrayList<Node> list, Integer distance) {
			this.currentPath = list;
			this.distance = distance;
		}
		
		public ComparableNode createNewBranch(Node newCity, Integer distance) {
			ArrayList<Node> newList = new ArrayList<Node>();
			newList.addAll(this.currentPath);
			newList.add(newCity);
			Integer newDistance = this.distance + distance;
			return new ComparableNode(newList, newDistance);
		}
		
			
		@Override
		public int compareTo(ComparableNode n) {
	        if(this.distance > n.distance) {
	            return 1;
	        } else if (this.distance < n.distance) {
	            return -1;
	        } else {
	            return 0;
	        }
		}
		
		@Override
		public String toString() {
			return currentPath.toString();
		}
		
	}
	
}

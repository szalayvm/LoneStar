import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.TreeSet;

import com.sun.glass.ui.CommonDialogs.Type;

public class RoadMap<T extends Comparable<? super T>> {
	private int size;
	public Hashtable<String, Node> referenceTable;
	public enum NodeType {CITY, LANDMARK, OTHER};
	public enum EdgeType {HIGHWAY, MAIN_ROAD, RURAL_ROAD, OTHER};
	public Hashtable<EdgeType, Integer> speedLimits;
	
	public RoadMap() {
		size = 0;
		referenceTable = new Hashtable<String, Node>();
		speedLimits = new Hashtable<EdgeType, Integer>();
		speedLimits.put(EdgeType.HIGHWAY, 75);
		speedLimits.put(EdgeType.MAIN_ROAD, 65);
		speedLimits.put(EdgeType.RURAL_ROAD, 55);
		speedLimits.put(EdgeType.OTHER, 45);
	}
	
	public int getSize() {
		return size;
	}
	
	public ArrayList<Node> getAllCities() {
		ArrayList<Node> a = new ArrayList<Node>();
		for(String s : referenceTable.keySet()) {
			a.add(referenceTable.get(s));
		}
		return a;
	}
	
	public ArrayList<Edge> getAllEdges() {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		TreeSet<Edge> e = new TreeSet<Edge>();
		
		ArrayList<Node> a = new ArrayList<Node>();
		for(String s : referenceTable.keySet()) {
			a.add(referenceTable.get(s));
		}
		for(Node n : a) {
			e.addAll(n.getConnectedRoads());
		}
		edges.addAll(e);
		
		return edges;
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
		ComparableNode first =  new ComparableNode(start, 0, (int) getStraightLineDistance(start, end));
		ArrayList<Node> connected = start.getConnectedCities();
		for(int i = 0; i < connected.size(); i++) {
			
			queue.add(first.createNewBranch(connected.get(i), (int) Math.round(start.connectedRoads.get(i).distance), (int) this.getStraightLineDistance(start.getConnectedCities().get(i), end)));
		}

		System.out.println("Initialized");
		System.out.println(queue);
		

		
		int index = 1;
		
		while(!queue.peek().currentPath.get(index).name.equals(end.name)) {

			connected = queue.peek().currentPath.get(index).getConnectedCities();
			
			ComparableNode saveCompare = queue.poll();
			Node saveNode = saveCompare.currentPath.get(index);
			for(int i = 0; i < connected.size(); i++) {
				if(!saveCompare.currentPath.contains(connected.get(i))){
					ComparableNode addToPath = saveCompare.createNewBranch(connected.get(i), (int) Math.round(saveNode.connectedRoads.get(i).distance),(int) this.getStraightLineDistance(saveNode.getConnectedCities().get(i), end));
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
		
		
		private Color color;
		
		public String getName() { return name; }
		public NodeType getType() { return type; }
		public double getLatitude() { return latitude; }
		public double getLongitude() { return longitude; }
		public Color getColor() { return color; }
		public void setColor(Color color) { this.color = color; }
		
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
		private double time;
		
		public Edge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
			this.firstNode = firstNode;
			this.secondNode = secondNode;
			
			this.firstNode.connectedRoads.add(this);
			this.secondNode.connectedRoads.add(this);
			
			this.name = name;
			this.type = type;
			this.distance = distance;
			this.time = this.distance / speedLimits.get(type); // Minutes
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
		Integer cost;
		Integer heuristic;
		
		public ComparableNode(Node node, Integer cost, Integer heuristic) {
			this.currentPath = new ArrayList<Node>();
			currentPath.add(node);
			this.cost = cost;
			this.heuristic = heuristic;
		}
		

		public ComparableNode(ArrayList<Node> list, Integer cost, Integer heuristic) {
			this.currentPath = list;
			this.cost = cost;
			this.heuristic = heuristic;
		}
		
		public ComparableNode createNewBranch(Node newCity, Integer cost, Integer heuristic) {
			ArrayList<Node> newList = new ArrayList<Node>();
			newList.addAll(this.currentPath);
			newList.add(newCity);
			Integer newDistance = this.cost + cost;
			return new ComparableNode(newList, newDistance, heuristic);
		}
		
			
		@Override
		public int compareTo(ComparableNode n) {
	        if(this.cost + this.heuristic > n.cost + n.heuristic) {
	            return 1;
	        } else if (this.cost + this.heuristic < n.cost + n.heuristic) {
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

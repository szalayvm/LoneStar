import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;


public class RoadMap<T extends Comparable<? super T>> {
	private int size;
	private Hashtable<String, Node> referenceTable;
	public enum NodeType {CITY, LANDMARK, OTHER};
	public enum EdgeType {HIGHWAY, MAIN_ROAD, RURAL_ROAD, OTHER};
	private final Hashtable<EdgeType, Integer> speedLimits;
	
	private static final int RADIUS_OF_THE_EARTH = 3959;
	
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
	
	public boolean addNode(String name, NodeType type, double latitude, double longitude) {
		referenceTable.put(name, new Node(name, type, latitude, longitude));
		return true;
	}
	
	
	public boolean addEdge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
		new Edge(firstNode, secondNode, name, type, distance);
		return true;
	}
	
	public ArrayList<Node> getAllCities() {
		ArrayList<Node> a = new ArrayList<Node>();
		
		for(String s : referenceTable.keySet()) { a.add(referenceTable.get(s)); }
		
		return a;
	}

	public ArrayList<Edge> getAllEdges() {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		TreeSet<Edge> e = new TreeSet<Edge>();
		
		ArrayList<Node> a = new ArrayList<Node>();
		for(String s : referenceTable.keySet()) { a.add(referenceTable.get(s)); }
		for(Node n : a) { e.addAll(n.getConnectedRoads()); }
		
		edges.addAll(e);
		return edges;
	}
		
	public Node getNodeFromString(String key) throws NullPointerException {
		Node value = referenceTable.get(key);
		if(value == null) {
			System.out.println("City " + key + " does not exist! Are you sure you spelled it right?");
			throw new NullPointerException();
		}
		return value;
	}
	
	public ArrayList<String> searchForCities(String input) {
		ArrayList<String> matches = new ArrayList<String>();
		
		for(String s: referenceTable.keySet()) if(s.contains(input)) matches.add(s);
		
		return matches;
	}
	
	public ArrayList<ArrayList<Node>> getNearCitiesToDistance(Node startingCity, int distance, int choices) {
		return getNearCities(startingCity, distance, choices, new WeightDistance());
	}
	
	public ArrayList<ArrayList<Node>> getNearCitiesToTime(Node startingCity, int time, int choices) {
		return getNearCities(startingCity, time, choices, new WeightTime());
	}
	
	public ArrayList<Node> findMinDistance(Node start, Node end) {
		return AStar(start, end, new HeuristicDistance(), new WeightDistance());
	}
	
	public ArrayList<Node> findMinTime(Node start, Node end) {
		return AStar(start, end, new HeuristicTime(), new WeightTime());
	}

		
	private ArrayList<Node> AStar(Node start, Node end, LambdaH h, LambdaW w) {
		
		PriorityQueue<ComparableNode> queue = new PriorityQueue<ComparableNode>(); 
		ComparableNode first =  new ComparableNode(start, 0, h.heuristic(start, end));
		ArrayList<Node> connected = start.getConnectedCities();
		for(int i = 0; i < connected.size(); i++) {	
			queue.add(first.createNewBranch(connected.get(i), w.weight(start.connectedRoads.get(i)), h.heuristic(start.getConnectedCities().get(i), end)));
		}
		
		int index = 1;
		while(!queue.peek().currentPath.get(index).name.equals(end.name)) {

			connected = queue.peek().currentPath.get(index).getConnectedCities();
			
			ComparableNode saveCompare = queue.poll();
			Node saveNode = saveCompare.currentPath.get(index);
			for(int i = 0; i < connected.size(); i++) {
				if(!saveCompare.currentPath.contains(connected.get(i))){
					ComparableNode addToPath = saveCompare.createNewBranch(connected.get(i), w.weight(saveNode.connectedRoads.get(i)), h.heuristic(saveNode.getConnectedCities().get(i), end));
					queue.add(addToPath);
				}
			}
			
			index = queue.peek().currentPath.size() - 1;
			
		}
		
		return queue.peek().currentPath;
	}
	
	private ArrayList<ArrayList<Node>> getNearCities(Node startingCity, int distance, int choices, LambdaW w) {
		
		ArrayList<ArrayList<Node>> selectedNodes = new ArrayList<ArrayList<Node>>();
		
		for(int i = 0; i < choices; i++) {
			
			int currentLength =  0;
			ArrayList<Node> visitedNodes = new ArrayList<Node>();
			ArrayList<Node> path = new ArrayList<Node>();
			Node currentNode = startingCity;
			Random r = new Random();
			
			path.add(currentNode);
			
			while(currentLength < distance) {
				visitedNodes.add(currentNode);
				int next = r.nextInt(currentNode.getConnectedRoads().size());
				Node nextNode = currentNode.getConnectedRoads().get(next).getOtherNode(currentNode);
				if(!visitedNodes.contains(nextNode)) {
					currentLength += w.weight(currentNode.getConnectedRoads().get(next));
					path.add(nextNode);
					currentNode = nextNode;
				}
			}
			path.remove(path.size() - 1);
			if(!selectedNodes.contains(path)) {
				selectedNodes.add(path);
			} else {
				i--;
			}
		}
		
		return selectedNodes;
	}
	
	private abstract class LambdaW { public abstract int weight(Edge e); }
	
	private class WeightDistance extends LambdaW { 
		public int weight(Edge e) { return (int) e.distance; } 
	}
	private class WeightTime extends LambdaW { 
		public int weight(Edge e) { return (int) e.time; } 
	}
		
	private abstract class LambdaH { public abstract int heuristic(Node node1, Node node2); } // Jank
	
	private class HeuristicDistance extends LambdaH {
		public int heuristic(Node node1, Node node2) {
			if(node1 == null | node2 == null) throw new NullPointerException();

			double lat1 = (Math.PI / 180) * (node1.latitude); // Convert to radians
			double lat2 = (Math.PI / 180) * (node2.latitude);
			double long1 = (Math.PI / 180) * (node1.longitude);
			double long2 = (Math.PI / 180) * (node2.longitude);
			double a = Math.pow((Math.sin((lat2 - lat1)) / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((long2 - long1) / 2) , 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double R = RADIUS_OF_THE_EARTH;
			
			return (int) (c*R);
		}
	}
	
	private class HeuristicTime extends LambdaH {
		public int heuristic(Node node1, Node node2) {
			return new HeuristicDistance().heuristic(node1, node2) * 60 / 75; // TURBO-JANK (also convert to minutes)
		}
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
			
			this.color = Color.BLACK;
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
	
	
	public class Edge implements Comparable<Edge> {
		private Node firstNode;
		private Node secondNode;
		
		private String name;
		private EdgeType type;
		private double distance;
		private double time;
		
		public Node getFirstNode() { return firstNode; }
		public Node getSecondNode() { return secondNode; }
		
		public Edge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
			this.firstNode = firstNode;
			this.secondNode = secondNode;
			
			if(this.firstNode != null) this.firstNode.connectedRoads.add(this);
			if(this.secondNode != null) this.secondNode.connectedRoads.add(this);
			
			this.name = name;
			this.type = type;
			this.distance = distance;
			if(this.type != null) this.time = this.distance / speedLimits.get(this.type) * 60; // Minutes
		}
		
		public Node getOtherNode(Node node) {
			if(firstNode.equals(node)) { return secondNode; }
			return firstNode;
		}
		
		public String toString() {
			return name;
		}
		@Override
		public int compareTo(Edge o) {
			if(this.distance > o.distance) {
				return 1;
			} else if (this.distance < o.distance) {
				return -1;
			} else {
				return 0;
			}
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

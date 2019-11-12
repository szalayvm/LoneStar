import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.TreeSet;


public class RoadMap<T extends Comparable<? super T>> {
	private int size;
	private Hashtable<String, Node> referenceTable;
	public enum NodeType {CITY, LANDMARK, PARK, OTHER};
	public enum EdgeType {HIGHWAY(75, Color.BLUE), MAIN_ROAD(65, Color.GREEN), RURAL_ROAD(55, Color.ORANGE), OTHER(45, Color.RED);
		private int speedLimit;
		private Color color;
		
		private EdgeType(int speedLimit, Color color) {this.speedLimit = speedLimit; this.color = color;}
		
		public int getSpeedLimit() {return speedLimit;}
		public Color getColor() {return color;}
	}

	private static final int RADIUS_OF_THE_EARTH = 3959;
	
	public RoadMap() {
		size = 0;
		referenceTable = new Hashtable<String, Node>();
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
		
	public Node getNodeFromString(String key) {
		Node value = referenceTable.get(key);
		return value;
	}
	
	public int getDistanceFromPath(ArrayList<Node> path) {
		return pathLength(path, new WeightDistance());
	}
	
	public int getTimeFromPath(ArrayList<Node> path) {
		return pathLength(path, new WeightTime());
	}
	
	public ArrayList<String> searchForCities(String input) {
		ArrayList<String> matches = new ArrayList<String>();
		
		for(String s: referenceTable.keySet()) if(s.toLowerCase().contains(input.toLowerCase())) matches.add(s);
		
		return matches;
	}
	
	public ArrayList<ArrayList<Node>> getNearCitiesToDistance(Node startingCity, int distance) {
		return getNearCities(startingCity, distance, new WeightDistance());
	}
	
	public ArrayList<ArrayList<Node>> getNearCitiesToTime(Node startingCity, int time) {
		return getNearCities(startingCity, time, new WeightTime());
	}
	
	public ArrayList<Node> findMinDistance(Node start, Node end) {
		return AStar(start, end, new HeuristicDistance(), new WeightDistance(), -1);
	}
	
	public ArrayList<Node> findMinTime(Node start, Node end) {
		return AStar(start, end, new HeuristicTime(), new WeightTime(), -1);
	}
	
	public ArrayList<Node> findMinTimeAccountingForTraffic(Node start, Node end, int startingTime) {
		return AStar(start, end, new HeuristicTime(), new WeightTimeAccountingForTraffic(), startingTime);
	}

	private int pathLength(ArrayList<Node> path, LambdaW w) {
		int length = 0;
		for(int i = 0; i<path.size() - 1; i++) {
			length += w.weight(getRoadBetweenCities(path.get(i), path.get(i + 1)), -1);
		}
		return length;
	}
	
	private Edge getRoadBetweenCities(Node node, Node node2) {
		int index = node.getConnectedCities().indexOf(node2);
		if(index != -1) {
			return node.getConnectedRoads().get(index);
		} 
		return null;
	}


	private ArrayList<Node> AStar(Node start, Node end, LambdaH h, LambdaW w, int startingTime) {
		
		PriorityQueue<ComparableNode> queue = new PriorityQueue<ComparableNode>(); 
		ComparableNode first =  new ComparableNode(start, 0, h.heuristic(start, end), startingTime);
		ArrayList<Node> connected = start.getConnectedCities();
		for(int i = 0; i < connected.size(); i++) {	
			queue.add(first.createNewBranch(connected.get(i), w.weight(start.connectedRoads.get(i), startingTime), h.heuristic(start.getConnectedCities().get(i), end), startingTime + w.weight(start.getConnectedRoads().get(i), startingTime)));
		}
		
		int index = 1;
		while(!queue.peek().currentPath.get(index).name.equals(end.name)) {

			connected = queue.peek().currentPath.get(index).getConnectedCities();
			
			ComparableNode saveCompare = queue.poll();
			Node saveNode = saveCompare.currentPath.get(index);
			for(int i = 0; i < connected.size(); i++) {
				if(!saveCompare.currentPath.contains(connected.get(i))){
					ComparableNode addToPath = saveCompare.createNewBranch(connected.get(i), w.weight(saveNode.connectedRoads.get(i), saveCompare.currentTime), h.heuristic(saveNode.getConnectedCities().get(i), end), saveCompare.currentTime);
					queue.add(addToPath);
				}
			}
			
			index = queue.peek().currentPath.size() - 1;
			
		}
		
		return queue.peek().currentPath;
	}
	
	private ArrayList<ArrayList<Node>> getNearCities(Node startingCity, int distance, LambdaW w) {
		
		ArrayList<ArrayList<Node>> selectedNodes = new ArrayList<ArrayList<Node>>();
		
		int currentDistance = 0;
		ArrayList<Node> path = new ArrayList<Node>();
		
		addCitiesToPath(path, currentDistance, distance, startingCity, selectedNodes, w);
		
		return selectedNodes;
	}
	
	private void addCitiesToPath(ArrayList<Node> path, int currentDistance, int maxDistance, Node city, ArrayList<ArrayList<Node>> paths, LambdaW w) {
		path.add(city);
		if(currentDistance <= maxDistance) {
			for(Edge e : city.getConnectedRoads()) {
				ArrayList<Node> newPath = new ArrayList<Node>();
				newPath.addAll(path);
				if(currentDistance + w.weight(e, -1) <= maxDistance) {
					addCitiesToPath(newPath, currentDistance + w.weight(e, -1), maxDistance, e.getOtherNode(city), paths, w);
				} else {
					paths.add(newPath);
					break;
				}
			}
		} else {
				ArrayList<Node> newPath = new ArrayList<Node>();
				newPath.addAll(path);
				paths.add(newPath);
		}
	}

	private abstract class LambdaW { public abstract int weight(Edge e, int time); }
	
	private class WeightDistance extends LambdaW { 
		public int weight(Edge e, int time) { return (int) e.distance; } 
	}
	private class WeightTime extends LambdaW { 
		public int weight(Edge e, int time) { return (int) e.time; } 
	}
	
	private class WeightTimeAccountingForTraffic extends LambdaW {
		public int weight(Edge e, int time) { 
			if(((360 < time % 1440 && time % 1440 < 480) | (1020 < time % 1440 && time % 1440 < 1140)) && e.type.equals(EdgeType.HIGHWAY)) {
				return (int) (1.5 * e.time);
			} else {
				return (int) e.time;
			}
		}
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
			this.time = this.distance / this.type.getSpeedLimit() * 60; // Minutes
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
		Integer currentTime;
		
		public ComparableNode(Node node, Integer cost, Integer heuristic, Integer startingTime) {
			this.currentPath = new ArrayList<Node>();
			currentPath.add(node);
			this.cost = cost;
			this.heuristic = heuristic;
			this.currentTime = startingTime;
		}
		

		public ComparableNode(ArrayList<Node> list, Integer cost, Integer heuristic, Integer newTime) {
			this.currentPath = list;
			this.cost = cost;
			this.heuristic = heuristic;
			this.currentTime = newTime;
		}
		
		public ComparableNode createNewBranch(Node newCity, Integer cost, Integer heuristic, Integer newTime) {
			ArrayList<Node> newList = new ArrayList<Node>();
			newList.addAll(this.currentPath);
			newList.add(newCity);
			Integer newDistance = this.cost + cost;
			Integer newNewTime = this.currentTime + newTime;
			return new ComparableNode(newList, newDistance, heuristic, newNewTime);
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

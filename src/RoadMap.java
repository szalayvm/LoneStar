import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class RoadMap<T extends Comparable<? super T>> {
	private int size;
	private Hashtable<String, Node> referenceTable;
	public enum NodeType {CITY, LANDMARK, PARK, OTHER};
	public enum EdgeType {HIGHWAY(75, Color.BLUE), MAIN_ROAD(55, Color.GREEN), RURAL_ROAD(55, Color.ORANGE), OTHER(45, Color.RED);
		
		private int speedLimit;
		private Color color;
		
		private EdgeType(int speedLimit, Color color) {
			this.speedLimit = speedLimit; 
			this.color = color;
		}
		
		public int getSpeedLimit() {return speedLimit;}
		public Color getColor() {return color;}
	}

	private static final int RADIUS_OF_THE_EARTH = 3959;
	
	private static final int RUSH_HOUR_START = 360;
	private static final int RUSH_HOUR_END = 480;
	private static final int RUSH_HOUR_START_2 = 1020;
	private static final int RUSH_HOUR_END_2 = 1140;
	private static final int MINUTES_IN_A_DAY = 1440;
	
	public RoadMap() {
		size = 0;
		referenceTable = new Hashtable<String, Node>();
	}
	
	//This method allows us to get the size of the map in other classes
	public int getSize() {
		return size;
	}
	
	//This method adds a node to our graph
	public boolean addNode(String name, NodeType type, double latitude, double longitude) {
		referenceTable.put(name, new Node(name, type, latitude, longitude));
		return true;
	}
	
	//This method adds an edge to our graph
	public boolean addEdge(Node firstNode, Node secondNode, String name, EdgeType type, double distance) {
		new Edge(firstNode, secondNode, name, type, distance);
		return true;
	}
	
	//This method gets an arraylist of all of the nodes in the graph
	public ArrayList<Node> getAllCities() {
		ArrayList<Node> a = new ArrayList<Node>();
		
		for(String s : referenceTable.keySet()) { a.add(referenceTable.get(s)); }
		
		return a;
	}

	//This method gets an arraylist of all of the edges in the graph
	public ArrayList<Edge> getAllEdges() {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		TreeSet<Edge> e = new TreeSet<Edge>();
		
		ArrayList<Node> a = new ArrayList<Node>();
		for(String s : referenceTable.keySet()) { a.add(referenceTable.get(s)); }
		for(Node n : a) { e.addAll(n.getConnectedRoads()); }
		
		edges.addAll(e);
		return edges;
	}
		
	//This method allows us to find the node from string
	//This method is used to convert the user input, a string, into something the code can use, a node.
	public Node getNodeFromString(String key) {
		Node value = referenceTable.get(key);
		return value;
	}
	
	//This method gets the distance of one road
	public int getDistanceFromPath(ArrayList<Node> path) {
		return pathLength(path, new WeightDistance());
	}
	
	//This method gets the time it takes to travel down one road
	public int getTimeFromPath(ArrayList<Node> path) {
		return pathLength(path, new WeightTime());
	}
	
	//This method searches through all of the locations to see if the input string is contained in any of the node titles
	//It is case-sensitive
	//This method gives the city searcher tab its functionality
	public ArrayList<String> searchForCities(String input) {
		ArrayList<String> matches = new ArrayList<String>();
		
		for(String s: referenceTable.keySet()) if(s.toLowerCase().contains(input.toLowerCase())) matches.add(s);
		
		return matches;
	}
	
	//This method finds the locations within a certain distance from the starting city
	//This method allows the trip planner to calculate by distance
	public ArrayList<ArrayList<Node>> getNearCitiesToDistance(Node startingCity, int distance) {
		return getNearCities(startingCity, distance, new WeightDistance());
	}
	
	//This method finds the locations that you can get to within a set amount of time
	//The method allows the trip planner to calculate by time
	public ArrayList<ArrayList<Node>> getNearCitiesToTime(Node startingCity, int time) {
		return getNearCities(startingCity, time, new WeightTime());
	}
	
	//This method finds the minimum distance between two nodes
	public ArrayList<Node> findMinDistance(Node start, Node end) {
		return AStar(start, end, new HeuristicDistance(), new WeightDistance(), -1);
	}
	
	//This method finds the minimum time to get between two nodes
	public ArrayList<Node> findMinTime(Node start, Node end) {
		return AStar(start, end, new HeuristicTime(), new WeightTime(), -1);
	}
	
	//This method find the minimum time between two nodes accounting for traffic
	public ArrayList<Node> findMinTimeAccountingForTraffic(Node start, Node end, int startingTime) {
		return AStar(start, end, new HeuristicTime(), new WeightTimeAccountingForTraffic(), startingTime);
	}

	//This method gets the between two nodes
	private int pathLength(ArrayList<Node> path, LambdaW w) {
		int length = 0;
		for(int i = 0; i<path.size() - 1; i++) {
			length += w.weight(getRoadBetweenCities(path.get(i), path.get(i + 1)), -1);
		}
		return length;
	}
	
	//If two cities are adjacent, it returns the edge that connects them
	private Edge getRoadBetweenCities(Node node, Node node2) {
		int index = node.getConnectedCities().indexOf(node2);
		if(index != -1) {
			return node.getConnectedRoads().get(index);
		} 
		return null;
	}

	//This method implements the a-star algorithm 
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
	
	//This method takes in the starting city and the weight and an arraylist of nodes that are reachable
	private ArrayList<ArrayList<Node>> getNearCities(Node startingCity, int distance, LambdaW w) {
		
		ArrayList<ArrayList<Node>> selectedNodes = new ArrayList<ArrayList<Node>>();
		
		int currentDistance = 0;
		ArrayList<Node> path = new ArrayList<Node>();
		
		addCitiesToPath(path, currentDistance, distance, startingCity, selectedNodes, w);
		
		return selectedNodes;
	}
	
	//The method finds which cities are reachable
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
	
	//This method notes which times are rush hour and accounts for how much longer traffic will take
	private class WeightTimeAccountingForTraffic extends LambdaW {
		public int weight(Edge e, int time) { 
			int t = (int) e.time;
			int d = (int) e.distance;
			int t1;
			
			boolean startInTraffic = false;
			boolean endInTraffic = false;
			
			if((RUSH_HOUR_START <= time % MINUTES_IN_A_DAY && RUSH_HOUR_END >= time % MINUTES_IN_A_DAY) | 
					(RUSH_HOUR_START_2 <= time % MINUTES_IN_A_DAY && RUSH_HOUR_END_2 >= time % MINUTES_IN_A_DAY)) {
				startInTraffic = true;
			}
			
			if(startInTraffic == true) {
				if((RUSH_HOUR_START <= (time + 2*t) % MINUTES_IN_A_DAY && RUSH_HOUR_END >= (time + 2*t) % MINUTES_IN_A_DAY) | 
						(RUSH_HOUR_START_2 <= (time + 2*t) % MINUTES_IN_A_DAY && RUSH_HOUR_END_2 >= (time + 2*t) % MINUTES_IN_A_DAY)) {
					endInTraffic = true;
				}
			} else {
				if((RUSH_HOUR_START <= (time + t) % MINUTES_IN_A_DAY && RUSH_HOUR_END >= (time + t) % MINUTES_IN_A_DAY) | 
						(RUSH_HOUR_START_2 <= (time + t) % MINUTES_IN_A_DAY && RUSH_HOUR_END_2 >= (time + t) % MINUTES_IN_A_DAY)) {
					endInTraffic = true;
				}
			}
			
			if((!startInTraffic && !endInTraffic) | !e.type.equals(EdgeType.HIGHWAY)) {
				return time + t;
			}
			
			if(!startInTraffic && endInTraffic) {
				t1 = Math.min(RUSH_HOUR_START, RUSH_HOUR_START_2) - (time % MINUTES_IN_A_DAY);
				return time + t1 + 2 * (60 * d / e.type.speedLimit - t1); // Starting time + time spent on the road before hitting rush hour + time spent on the road after hitting rush hour
			}
			
			if(!endInTraffic && startInTraffic) {
				t1 = Math.min(RUSH_HOUR_END, RUSH_HOUR_END_2) - (time % MINUTES_IN_A_DAY);
				return time + t1 + (60 * d / e.type.speedLimit - t1/2); // Ditto
			}
			
			return time + 2 * t; // In Rush Hour the entire time

		}
	}
		
	private abstract class LambdaH { public abstract int heuristic(Node node1, Node node2); }
	
	//This calculates the distance from the latitude and longitude
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
			return new HeuristicDistance().heuristic(node1, node2) * 60 / EdgeType.HIGHWAY.speedLimit;
		}
	}

	//This class creates the nodes
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
	
	//This class creates the edges needed to make the graph functional
	public class Edge implements Comparable<Edge> {
		private Node firstNode;
		private Node secondNode;
		
		private String name;
		private EdgeType type;
		private double distance;
		private double time;
		
		//These methods allow other classes to access the nodes related to the edge
		public Node getFirstNode() { return firstNode; }
		public Node getSecondNode() { return secondNode; }
		
		//This is the constructor, which creates the edge
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
		
		//This allows makes sure that we can access both nodes
		public Node getOtherNode(Node node) {
			if(firstNode.equals(node)) { return secondNode; }
			return firstNode;
		}
		
		//This method allows us to get the node as a string
		public String toString() {
			return name;
		}
		
		//This method compares this edges to other edges
		@Override
		public int compareTo(Edge o) {
			
			if((firstNode.equals(o.firstNode) && secondNode.equals(o.secondNode)) | (firstNode.equals(o.secondNode) && secondNode.equals(o.firstNode))) {
				return 0;
			} else {
				return -1;
			}
		}
	}
	
	//This class creates the nodes needed to make the graph functional
	private class ComparableNode implements Comparable<ComparableNode> {
		ArrayList<Node> currentPath;
		Integer cost;
		Integer heuristic;
		Integer currentTime;
		
		//The is the constructor, which creates the node given certain input
		public ComparableNode(Node node, Integer cost, Integer heuristic, Integer startingTime) {
			this.currentPath = new ArrayList<Node>();
			currentPath.add(node);
			this.cost = cost;
			this.heuristic = heuristic;
			this.currentTime = startingTime;
		}
		
		//This construction makes the a comparable node given a list of nodes
		public ComparableNode(ArrayList<Node> list, Integer cost, Integer heuristic, Integer newTime) {
			this.currentPath = list;
			this.cost = cost;
			this.heuristic = heuristic;
			this.currentTime = newTime;
		}
		
		//This creates a ArrayList of nodes
		public ComparableNode createNewBranch(Node newCity, Integer cost, Integer heuristic, Integer newTime) {
			ArrayList<Node> newList = new ArrayList<Node>();
			newList.addAll(this.currentPath);
			newList.add(newCity);
			Integer newDistance = this.cost + cost;
			Integer newNewTime = this.currentTime + newTime;
			return new ComparableNode(newList, newDistance, heuristic, newNewTime);
		}
		
		//This method compares two nodes
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

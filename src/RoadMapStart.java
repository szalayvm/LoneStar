import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class RoadMapStart {
	
	//The main method creates a road map, gets the locatations and roads from a file, and creates the GUI frame
	public static void main(String[] args) throws IOException {
		RoadMap g = new RoadMap();		
		getLocationsFromFile(g);
		getRoadsFromFile(g);
		GUIFrame.create(g);
	}

	//This method takes in a road map and scans a file for the location type and location and stores them to the roadmap.
	private static void getLocationsFromFile(RoadMap g) {
		try {
			Scanner s = new Scanner(new File("src/location_database.txt"));
			while(s.hasNextLine()) {
				String name = s.nextLine();
				String locationtype = s.next();
				RoadMap.NodeType nodetype;
				if(locationtype.equals("City")) {
					nodetype = RoadMap.NodeType.CITY;
				} else if(locationtype.equals("Landmark")) {
					nodetype = RoadMap.NodeType.LANDMARK;
				} else if(locationtype.equals("Park")){
					nodetype = RoadMap.NodeType.PARK;
				} else {
					nodetype = RoadMap.NodeType.OTHER;
				}
				double latitude = s.nextDouble();
				double longitude = s.nextDouble();
				
				g.addNode(name, nodetype, latitude, longitude);
				if(s.hasNextLine()) {
					s.nextLine();
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println(":( File not found! Was it deleted?");
			e.printStackTrace();
		}
		
	}

	//This method takes in a file of all the roads and stores them in the roadmap. 
	//It keeps track of the type of the road
	private static void getRoadsFromFile(RoadMap g) {
		try {
			Scanner s2 = new Scanner(new File("src/road_database.txt"));
			while(s2.hasNextLine()) {
				String name = s2.nextLine();
				String city1 = s2.nextLine();
				String city2 = s2.nextLine();
				String roadtype = s2.next();
				RoadMap.EdgeType edgetype;
				if(roadtype.equals(("Highway"))) {
					edgetype = RoadMap.EdgeType.HIGHWAY;
				} else if(roadtype.equals("MainRoad")) {
					edgetype = RoadMap.EdgeType.MAIN_ROAD;
				} else if(roadtype.equals("RuralRoad")) {
					edgetype = RoadMap.EdgeType.RURAL_ROAD;
				} else {
					edgetype = RoadMap.EdgeType.OTHER;
				}
				double distance = s2.nextDouble();
				
				g.addEdge(g.getNodeFromString(city1), g.getNodeFromString(city2), name, edgetype, distance);
				if(s2.hasNextLine()) {
					s2.nextLine();
				}
			}
			s2.close();
		} catch (FileNotFoundException e) {
			System.out.println(":( File not found! Was it deleted?");
			e.printStackTrace();
		}
		
	}
}

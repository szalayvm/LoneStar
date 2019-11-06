import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;


public class MapVisualizer extends JPanel {
	public RoadMap b;
	public ArrayList<RoadMap.Node> cities;
	
	public MapVisualizer (RoadMap m){
		this.b= m; 
		this.cities= m.getAllCities();
		this.cities= m.getAllCities();
		this.cities.get(0);
		
		
	}
	
	
	
	public void paintCompenents(Graphics g){
		super.paintComponent(g);
		
		
		
		
		
	}

}

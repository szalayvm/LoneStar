import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class MapVisualizer {
	public RoadMap b;
	public ArrayList<RoadMap.Node> cities;

	public MapVisualizer(RoadMap m) {
		this.b = m;
		this.cities = m.getAllCities();
		this.cities = m.getAllCities();
		// this.cities.get(0);
		this.draw();

	}
	
	class Window extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			for (RoadMap.Node n : cities) {
				g.setColor(n.getColor());
				g.drawOval(40, 50, 10, 10);
			}
		}
	}

	public Window draw() {

		return new Window();
		

	}
	
	
}

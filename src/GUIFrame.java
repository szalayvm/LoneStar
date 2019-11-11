import java.awt.Dimension;

import javax.swing.JFrame;

public class GUIFrame {
	private static final int WIDTH = 1500;
	private static final int HEIGHT = 1500;
	
	
	public static void create(RoadMap m ){
		JFrame f = new JFrame("Trip Planner");
		f.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		f.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		f.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		f.setResizable(false);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new MapVisualizer(m)); 
		f.setVisible(true);
		
		
	}

}

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MapVisualizer extends JPanel {
	public RoadMap b;
	public ArrayList<RoadMap.Node> cities;
	public int w;
	public int h;
	public JTabbedPane tab;
	public static final int radius = 25;
	public ArrayList<RoadMap.Edge> edges;
	public ArrayList<RoadMap.Node> result;
	public JTextArea area;

	public MapVisualizer(RoadMap m) {
		this.result = new ArrayList<RoadMap.Node>();
		this.setLayout(null);
		this.w = 900;
		this.h = 900;
		this.b = m;
		this.cities = m.getAllCities();
		this.edges = m.getAllEdges();
		this.tab = new JTabbedPane();
		this.makeTitle();
		this.tripPlanner();
		this.shortRoute();
		this.searcher();
		this.tab.setSize(w / 2, h / 2);
		this.tab.setLocation(200, 100);
		this.add(tab, BorderLayout.SOUTH);
		this.area = new JTextArea();
		this.area.setSize(500, 500);
		this.area.setLocation(3 * w / 4, 80);
		this.area.setLineWrap(true);
		area.setWrapStyleWord(true);
		this.add(this.area);
	}

	public void makeTitle() {
		JLabel titleText = new JLabel("Lone Star Traversal");
		titleText.setLocation(w / 2 - 100, -h / 2 + 50);
		titleText.setSize(w, h);
		titleText.setFont(new Font("Freestyle Script", 1, 30));

		this.add(titleText);
	}

	private void tripPlanner() {
		GridLayout TPlayout = new GridLayout(4, 2);

		JLabel startlabel = this.makeLabel("Starting City: ");
		JLabel endlabel = this.makeLabel("Ending City:");
		JTextField start = this.makeField("Enter Your Starting City");
		JLabel timeLabel = this.makeLabel("Time: ");
		JTextField time = this.makeField("0");
		JLabel distanceLabel = this.makeLabel("Distance: ");
		JTextField distance = this.makeField("0");
		JButton timeButton = new JButton("Calculate Based on Time");
		JButton distanceButton = new JButton("Calculate Based on Distance");

		timeButton.addActionListener(new TPtimeListener(start, time));
		distanceButton.addActionListener(new TPdistanceListener(start, distance));

		JPanel TPPanel = new JPanel();
		TPPanel.setOpaque(true);
		TPPanel.setBackground(Color.white);
		TPPanel.setSize(200, 200);
		TPPanel.setLayout(TPlayout);
		TPPanel.add(startlabel);
		TPPanel.add(start);
		TPPanel.add(timeLabel);
		TPPanel.add(distanceLabel);
		TPPanel.add(time);
		TPPanel.add(distance);
		TPPanel.add(timeButton);
		TPPanel.add(distanceButton);

		tab.addTab("Trip Planner", TPPanel);

	}

	private void searcher() {
		JTextField searcher = this.makeField("Enter in a character and see which city you want!");
		this.tab.addTab("City Searcher", searcher);

	}

	public void shortRoute() {
		GridLayout SRlayout = new GridLayout(6, 0);

		JPanel SRPanel = new JPanel();
		SRPanel.setOpaque(true);
		SRPanel.setBackground(Color.white);
		SRPanel.setSize(200, 200);
		SRPanel.setLayout(SRlayout);

		JLabel startlabel = this.makeLabel("Starting City: ");
		JLabel endlabel = this.makeLabel("Ending City:");

		JTextField start = this.makeField("Enter Your Starting City");
		start.addActionListener(new TextFieldListener(start));
		JTextField end = this.makeField("Enter Your Ending City");

		JButton SRdistance = new JButton("Find Based on Distance");
		JButton SRtime = new JButton("Find Based on Time");
		SRdistance.addActionListener(new SRdistanceListener(start, end));
		SRtime.addActionListener(new SRtimeListener(start, end));

		SRPanel.add(startlabel);
		SRPanel.add(start);
		SRPanel.add(endlabel);
		SRPanel.add(end);
		SRPanel.add(SRdistance);
		SRPanel.add(SRtime);

		tab.addTab("Shortest Route", SRPanel);

	}

	public enum TextAlignment {
		Top_LEFT, Top,
	}

	@Override
	public void paintComponent(Graphics g) {// Bottom right is Galv

		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		int d = 30;
		double minlat = this.cities.get(0).getLatitude();
		double maxlong = this.cities.get(0).getLongitude();

		for (RoadMap.Node n : cities) {
			if (maxlong > n.getLongitude()) {
				maxlong = n.getLongitude();
			}
			if (minlat < n.getLatitude()) {
				minlat = n.getLatitude();
			}

		}

		for (RoadMap.Edge e : edges) {
			int x1 = 1300 - (int) (e.getFirstNode().getLongitude() * 100 - maxlong * 100) + this.radius / 2;
			int y1 = 600 + (int) (minlat * 30 - e.getFirstNode().getLatitude() * 30) + this.radius / 2;

			int x2 = 1300 - (int) (e.getSecondNode().getLongitude() * 100 - maxlong * 100) + this.radius / 2;
			int y2 = 600 + (int) (minlat * 30 - e.getSecondNode().getLatitude() * 30) + this.radius / 2;
			g.drawLine(x1, y1, x2, y2);

		}

		for (RoadMap.Node n : cities) {
			ArrayList<RoadMap.Edge> edges = n.getConnectedRoads();
			g.setColor(n.getColor());

			int x = 1300 - (int) (n.getLongitude() * 100 - maxlong * 100);
			int y = 600 + (int) (minlat * 30 - n.getLatitude() * 30);

			g.fillOval(x, y, radius, radius);

			g.setFont(new Font("Times New Roman", Font.BOLD, 15));
			g.setColor(Color.BLUE);
			if (n.getName().equals("Fort Worth")) {
				g.drawString(n.getName(), x - 25, y);
			} else {
				g.drawString(n.getName(), x, y);
			}

		}

		Rectangle bound = new Rectangle(3 * w / 4, 80, 200, 200);
		g.setFont(new Font("Times New Roman", 1, 20));

		this.area.setText("Optimal Path: \n" + result.toString());
		this.area.setFont(new Font("Times New Roman", 1, 15));

	}

	public JLabel makeLabel(String s) {
		// JLabel startlabel = new JLabel();
		//
		// startlabel.setBackground(Color.cyan);
		return new JLabel(s);
		// JLabel endlabel = new JLabel("Ending City: ");
		// endlabel.setBackground(new Color(153, 255, 102));

	}

	public JTextField makeField(String s) {
		return new JTextField(s);
	}

	// make null checks on buttons

	class SRdistanceListener implements ActionListener {
		JTextField start;
		JTextField end;

		SRdistanceListener() {
		}

		SRdistanceListener(JTextField start, JTextField end) {
			this.start = start;
			this.end = end;
		}

		public void actionPerformed(ActionEvent e) {

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText());
			RoadMap.Node endNode = b.getNodeFromString(this.end.getText());

			result = b.findMinDistance(startNode, endNode);
			repaint();
		}
	}

	class SRtimeListener implements ActionListener {
		JTextField start;
		JTextField end;

		SRtimeListener(JTextField start, JTextField end) {
			this.start = start;
			this.end = end;
		}

		public void actionPerformed(ActionEvent e) {

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText());
			RoadMap.Node endNode = b.getNodeFromString(this.end.getText());

			result = b.findMinTime(startNode, endNode);
			repaint();
		}
	}

	class TPtimeListener implements ActionListener {
		JTextField start;
		JTextField time;

		TPtimeListener(JTextField start, JTextField time) {
			this.start = start;
			this.time = time;
		}

		public void actionPerformed(ActionEvent e) {

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText());
			int timenum = Integer.parseInt(this.time.getText());

			result = b.getNearCitiesToTime(startNode, timenum);
			repaint();
		}
	}

	class TPdistanceListener implements ActionListener {
		JTextField start;
		JTextField distance;

		TPdistanceListener() {
		}

		TPdistanceListener(JTextField start, JTextField distance) {
			this.start = start;
			this.distance = distance;
		}

		public void actionPerformed(ActionEvent e) {

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText());
			int distancenum = Integer.parseInt(this.distance.getText());

			result = b.getNearCitiesToDistance(startNode, distancenum);
			repaint();
		}
	}
}

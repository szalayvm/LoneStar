import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
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
	public JTextArea oArea;
	public double shortestTime;
	public JTextArea sdArea;
	public JTextArea tArea;
	public ArrayList<ArrayList<RoadMap.Node>> tpR;
	public boolean TP;
	public JTextArea tpArea;
	public Color red = new Color(222, 70, 70);
	public Color blue = new Color(69, 126, 216);

	public MapVisualizer(RoadMap m) {
		this.TP = false;
		this.shortestTime = 0;
		this.result = new ArrayList<RoadMap.Node>();
		this.tpR = new ArrayList<ArrayList<RoadMap.Node>>();
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
		this.tab.setLocation(10, 100);
		this.add(tab, BorderLayout.SOUTH);

		this.sdArea = new JTextArea();
		this.sdArea.setSize(500, 150);
		this.sdArea.setLocation(10 + w / 2 + 10, 80);

		this.tArea = new JTextArea();
		this.tArea.setSize(500, 150);
		this.tArea.setLocation(10 + w / 2 + 10, 80 + 150);

		this.oArea = new JTextArea();
		this.oArea.setSize(500, 150);
		this.oArea.setLocation(3 * w / 4, 80);

		this.tpArea = new JTextArea();
		this.tpArea.setSize(500, 150);
		this.tpArea.setLocation(3 * w / 4, 150 + 80);

		this.oArea.setLineWrap(true);
		this.oArea.setWrapStyleWord(true);
		this.tpArea.setLineWrap(true);
		this.tpArea.setWrapStyleWord(true);

		this.add(this.oArea);
		this.add(this.sdArea);
		this.add(this.tArea);
		this.add(this.tpArea);

	}

	public void makeTitle() {
		JLabel titleText = new JLabel("Lone Star Traversal");
		titleText.setLocation(1500 / 2 - 75, -h / 2 + 50);
		titleText.setSize(w, h);
		titleText.setFont(new Font("Freestyle Script", 1, 38));

		this.add(titleText);
	}

	private void tripPlanner() {
		GridLayout TPlayout = new GridLayout(4, 2);

		JLabel startlabel = this.makeLabel("Starting City: ");
		JLabel endlabel = this.makeLabel("Ending City:");
		JTextField start = this.makeField("Enter Your Starting City");
		JLabel timeLabel = this.makeLabel("Time (hours): ");
		JTextField time = this.makeField("0");
		JLabel distanceLabel = this.makeLabel("Distance (miles): ");
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
		GridLayout searchG = new GridLayout(2, 0);
		JTextField searcher = new JTextField("Enter in a character and see which city you want!");

		JPanel searchP = new JPanel();
		JTextArea output = new JTextArea("Possible Cities: ");
		output.setLineWrap(true);
		output.setWrapStyleWord(true);

		searcher.addActionListener(new cityListener(searcher, output));
		searchP.setLayout(searchG);

		searchP.add(searcher);
		searchP.add(output);

		this.tab.addTab("City Searcher", searchP);

	}

	public void shortRoute() {
		GridLayout SRlayout = new GridLayout(8, 0);

		JPanel SRPanel = new JPanel();
		SRPanel.setOpaque(true);
		SRPanel.setBackground(Color.white);
		SRPanel.setSize(200, 200);
		SRPanel.setLayout(SRlayout);

		JLabel startlabel = this.makeLabel("Starting City: ");
		startlabel.setOpaque(true);
		startlabel.setBackground(blue);
		JLabel endlabel = this.makeLabel("Ending City:");
		endlabel.setOpaque(true);
		endlabel.setBackground(red);
		JLabel timelabel = this.makeLabel("Enter in the military time :");

		JTextField start = this.makeField("Enter Your Starting City");
		start.addActionListener(new TextFieldListener(start));
		//start.setBackground(blue);
		JTextField end = this.makeField("Enter Your Ending City");
		end.setBackground(blue);
		JTextField time = this.makeField("Enter in the Time of Day");
		time.setBackground(red);

		JButton SRdistance = new JButton("Find Based on Distance");
		JButton SRtime = new JButton("Find Based on Time");
		SRdistance.addActionListener(new SRdistanceListener(start, end));
		SRtime.addActionListener(new SRtimeListener(start, end));

		SRPanel.add(startlabel);
		SRPanel.add(start);
		SRPanel.add(endlabel);
		SRPanel.add(end);
		SRPanel.add(timelabel);
		SRPanel.add(time);
		SRPanel.add(SRdistance);
		SRPanel.add(SRtime);

		tab.addTab("Shortest Route", SRPanel);

	}

	@Override
	public void paintComponent(Graphics g) {// Bottom right is Galv

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

		Font f = new Font("Times New Roman", 1, 18);

		this.oArea.setFont(f);
		this.tpArea.setFont(f);
		this.tArea.setFont(f);
		this.sdArea.setFont(f);

		if (tpR.size() > 5) {
			this.tpArea.setText("Possible Paths: \n" + tpR.subList(0, 5).toString());
		} else
			this.tpArea.setText("Possible Paths: \n" + tpR.toString());

		this.oArea.setText("Optimal Path: \n" + result.toString());

		this.sdArea.setText("Distance: \n" + b.getDistanceFromPath(result) + " miles");

		double ht = (int) b.getTimeFromPath(result) / 60;
		double mt = b.getTimeFromPath(result) - ht * 60;
		this.tArea.setText("Time: \n" + ht + " hours " + mt + " minutes");

		if (TP == true) {
			this.tArea.setText("Time: \n" + 0 + " hours " + 0.0 + " minutes");
			this.oArea.setText("Optimal Path: \n" + "[]");

			this.sdArea.setText("Distance: \n" + 0.0 + " miles");

		}
		TP = false;

		setNodeBlack();
		setTPBlack();
		result.clear();
		tpR.clear();

	}

	public JLabel makeLabel(String s) {

		return new JLabel(s);

	}

	public JTextField makeField(String s) {
		return new JTextField(s);
	}

	public void changeNodeColor() {
		if (!result.isEmpty()) {
			for (RoadMap.Node n : result) {
				n.setColor(Color.RED);
			}
		}
	}

	public void setNodeBlack() {

		for (RoadMap.Node n : result) {
			n.setColor(Color.BLACK);
		}

	}

	public void setTPRed() {
		if (!tpR.isEmpty()) {
			for (ArrayList<RoadMap.Node> a : tpR) {
				for (RoadMap.Node n : a) {
					n.setColor(Color.RED);
				}
			}
		}
	}

	public void setTPBlack() {
		if (!tpR.isEmpty()) {
			for (ArrayList<RoadMap.Node> a : tpR) {
				for (RoadMap.Node n : a) {
					n.setColor(Color.BLACK);
				}
			}
		}
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

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText().trim());
			RoadMap.Node endNode = b.getNodeFromString(this.end.getText().trim());

			if (startNode != null && endNode != null) {
				result = b.findMinDistance(startNode, endNode);
				changeNodeColor();
				repaint();
			} else {
				oArea.setText("Your landmark or city was not found. Check your spelling!");
			}
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

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText().trim());
			RoadMap.Node endNode = b.getNodeFromString(this.end.getText().trim());
			if (startNode != null && endNode != null) {
				result = b.findMinTime(startNode, endNode);
				changeNodeColor();
				repaint();
			} else {
				oArea.setText("Your landmark or city was not found. Check your spelling!");
			}
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
			TP = true;

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText().trim());
			int timenum = Integer.parseInt((this.time.getText().trim()));
			if (startNode != null) {

				tpR = b.getNearCitiesToTime(startNode, (int) (timenum * 60));
				setTPRed();
				repaint();
			} else {
				oArea.setText("Your landmark or city was not found. Check your spelling!");
			}

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
			TP = true;

			RoadMap.Node startNode = b.getNodeFromString(this.start.getText().trim());

			int distancenum = Integer.parseInt(this.distance.getText().trim());
			if (startNode != null) {
				tpR = b.getNearCitiesToDistance(startNode, distancenum);
				setTPRed();
				repaint();
			} else {
				oArea.setText("Your landmark or city was not found. Check your spelling!");
			}

		}
	}

	class cityListener implements ActionListener {
		public JTextField i;
		public JTextArea o;

		public cityListener(JTextField searcher, JTextArea output) {
			this.i = searcher;
			this.o = output;

		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			this.o.setText("Possible Cities: \n" + b.searchForCities(this.i.getText()).toString());

		}

	}

}

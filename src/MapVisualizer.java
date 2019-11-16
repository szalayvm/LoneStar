import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class MapVisualizer extends JPanel {
	public RoadMap b;
	public ArrayList<RoadMap.Node> cities;
	public int w = 900;
	public int h = 900;
	public JTabbedPane tab;
	public static final int radius = 25;
	public ArrayList<RoadMap.Edge> edges;
	public ArrayList<RoadMap.Node> result =  new ArrayList<RoadMap.Node>();;
	public JTextArea oArea;
	public double shortestTime = 0;
	public JTextArea sdArea;
	public JTextArea tArea;
	public ArrayList<ArrayList<RoadMap.Node>> tpR = new ArrayList<ArrayList<RoadMap.Node>>();
	public boolean TP = false;
	public JTextArea tpArea;
	public Color red = new Color(222, 70, 70);
	public Color blue = new Color(69, 126, 216);
	public Border textborder = BorderFactory.createCompoundBorder();
	public Border border = BorderFactory.createRaisedBevelBorder();
	private BufferedImage star;

	//This constructs the map, setting the size of the text feilds on the map, setting up the tabs, and calling other methods to set up other parts on the map
 	public MapVisualizer(RoadMap m) {
		this.setLayout(null);
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

	//This method makes the title appears at the right location on the GUI
	public void makeTitle() {
		JLabel titleText = new JLabel("Lone Star Traversal");
		titleText.setLocation(1500 / 2 - 75, -h / 2 + 50);
		titleText.setSize(w, h);
		titleText.setFont(new Font("Freestyle Script", 1, 38));
		
		try {
			File img = new File("LoneStarImage.png");
			this.star = ImageIO.read(img);
		} catch (IOException e) {
			System.out.println("The image file doesn't exist! Was it deleted?");
			e.printStackTrace();
		}
		this.add(titleText);
	}

	//This method makes the trip planner tab work. It sets up the text fields and buttons, and it calls the methods to give the buttons functionality
	private void tripPlanner() {
		GridLayout TPlayout = new GridLayout(4, 2);
		

		JLabel startlabel = new JLabel("Starting Location: ");
		//JLabel startlabel = this.makeLabel("Starting Location: ");
		startlabel.setOpaque(true);
		startlabel.setBackground(new Color(67, 130, 231));
		JTextField start = new JTextField("Enter Your Starting Location");
		//JTextField start = this.makeField("Enter Your Starting Location");
		start.setBackground(new Color(67, 130, 231));
		JLabel timeLabel = new JLabel("Time (hour): ");
		//JLabel timeLabel = this.makeLabel("Time (hours): ");
		timeLabel.setOpaque(true);
		timeLabel.setBackground(new Color(52, 68, 238));
		JTextField time = new JTextField("0");
		//JTextField time = this.makeField("0");
		time.setBackground(new Color(114, 131, 218));
		JLabel distanceLabel = new JLabel("Distance (miles): ");
		//JLabel distanceLabel = this.makeLabel("Distance (miles): ");
		distanceLabel.setOpaque(true);
		distanceLabel.setBackground(new Color(52, 68, 238));
		JTextField distance = new JTextField("0");
		//JTextField distance = this.makeField("0");
		distance.setBackground(new Color(114, 131, 218));
		JButton timeButton = new JButton("Calculate Based on Time");
		JButton distanceButton = new JButton("Calculate Based on Distance");

		timeButton.addActionListener(new TPtimeListener(start, time));
		distanceButton.addActionListener(new TPdistanceListener(start, distance));

		start.setBorder(border); 
		distance.setBorder(border);
		time.setBorder(border);
		
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

	//This method makes the search tab work.
	//It sets up two text fields, one for the input and one for the output
	//It also calls the methods to give the text fields functionality
	private void searcher() {
		Font f = new Font("Times New Roman", 1, 18);
		GridLayout searchG = new GridLayout(2, 0);
		JTextField searcher = new JTextField("Enter in a character and see which location you want!");
		searcher.setFont(f);
		searcher.setBackground(blue);

		JPanel searchP = new JPanel();
		JTextArea output = new JTextArea("Possible Locations: ");
		output.setFont(f);
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		output.setBackground(red);

		searcher.addActionListener(new cityListener(searcher, output));
		searchP.setLayout(searchG);

		searchP.add(searcher);
		searchP.add(output);

		this.tab.addTab("Location Searcher", searchP);

	}

	//This method makes the trip planner tab work
	public void shortRoute() {
		GridLayout SRlayout = new GridLayout(9, 0);

		JPanel SRPanel = new JPanel();
		SRPanel.setOpaque(true);
		SRPanel.setBackground(Color.white);
		SRPanel.setSize(200, 200);
		SRPanel.setLayout(SRlayout);

		JLabel startlabel = new JLabel ("Staring Location: ");
		//JLabel startlabel = this.makeLabel("Starting Location: ");
		startlabel.setOpaque(true);
		startlabel.setBackground(blue);
		JLabel endlabel = new JLabel ("Ending Location: ");
		//JLabel endlabel = this.makeLabel("Ending Location:");
		endlabel.setOpaque(true);
		endlabel.setBackground(red);

		JLabel timelabel = new JLabel("Enter in the military time: ");
		//JLabel timelabel = this.makeLabel("Enter in the military time :");

		JTextField start = new JTextField("Enter Your Starting Location");
		//JTextField start = this.makeField("Enter Your Starting Location");
		start.addActionListener(new TextFieldListener(start));
		// start.setBackground(blue);
		JTextField end = new JTextField("Enter Your Ending Location");
		//JTextField end = this.makeField("Enter Your Ending Location");
		end.setBackground(blue);
		JTextField time = new JTextField("Enter in the Time of Day");
		//JTextField time = this.makeField("Enter in the Time of Day");
		time.setBackground(red);
		

		JButton SRdistance = new JButton("Find Based on Distance");
		SRdistance.setBackground(red);
		JButton SRtime = new JButton("Find Based on Time");
		SRtime.setBackground(new Color(255,255,255));
		JButton SRtraff = new JButton("Calculate Based on Traffic");
		SRtraff.setBackground(blue);
		
		SRdistance.addActionListener(new SRdistanceListener(start, end));
		SRtime.addActionListener(new SRtimeListener(start, end));
		SRtraff.addActionListener(new militaryTimeLis(start, end, time));

		start.setBorder(textborder);
		end.setBorder(textborder);
		time.setBorder(textborder);
		SRdistance.setBorder(border);
		SRtime.setBorder(border);
		SRtraff.setBorder(border);
		
		SRPanel.add(startlabel);
		SRPanel.add(start);
		SRPanel.add(endlabel);
		SRPanel.add(end);
		SRPanel.add(timelabel);
		SRPanel.add(time);
		SRPanel.add(SRdistance);
		SRPanel.add(SRtime);
		SRPanel.add(SRtraff);

		tab.addTab("Shortest Route", SRPanel);

	}

	//This method makes everything appear on the gui
	@Override
	public void paintComponent(Graphics g) {// Bottom right is Galv

		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		//int d = 30;
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
		
		//The next two lines draw stars next to the title
		g.drawImage(star, (1500 / 2 - 75) - 75, 0, 75, 75, this);
		g.drawImage(star, (1500 / 2 - 75) + 250, 0, 75, 75, this);

		//This for loop draws all of the edges on screen
		for (RoadMap.Edge e : edges) {
			int x1 = 1300 - (int) (e.getFirstNode().getLongitude() * 100 - maxlong * 100) + this.radius / 2;
			int y1 = 600 + (int) (minlat * 30 - e.getFirstNode().getLatitude() * 30) + this.radius / 2;
			int x2 = 1300 - (int) (e.getSecondNode().getLongitude() * 100 - maxlong * 100) + this.radius / 2;
			int y2 = 600 + (int) (minlat * 30 - e.getSecondNode().getLatitude() * 30) + this.radius / 2;
			g.drawLine(x1, y1, x2, y2);
		}

		//This for loop draws all of the cities on screen
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
		//This section of code sets the fonts
		Font f = new Font("Times New Roman", 1, 18);
		this.oArea.setFont(f);
		this.tpArea.setFont(f);
		this.tArea.setFont(f);
		this.sdArea.setFont(f);

		//The following code writes the solutions in the correct text field
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
		//reset the variables, ready for next change
		TP = false;
		setNodeBlack();
		setTPBlack();
		result.clear();
		tpR.clear();

	}

//	public JLabel makeLabel(String s) {
//
//		return new JLabel(s);
//
//	}
//
//	public JTextField makeField(String s) {
//		return new JTextField(s);
//	}

	//This following four methods can set the colors of the nodes
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

	//This method gives the calculate based on distance button in the Shortest Route tab functionality
	//It calls the findMinDistance method from roadmap
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

	//This methods gives to calculate based on time button in the Shortest Route tab functionality
	//Its calls the findMinTime method from roadmap
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

	//This method gives the calculate based on time button in Trip Planner functionality
	//It calls the getNearCitesToTime method from roadmap
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

	//This method gives the calculate based on distance button in Trip Planner functionality
	//It calls the getNearCitiesToDistance function from roadmap
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

	//This method gives the location searcher tab functionality
	//It calls the searchForCities method from roadmap
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

	//This method gives the button to calculate based on traffic functionality.
	//It class the findMinTimeAccountingForTraffic method from roadmap
	class militaryTimeLis implements ActionListener {
		public JTextField start;
		public JTextField end;
		public JTextField time;

		public militaryTimeLis(JTextField start, JTextField end, JTextField time) {
			this.start = start;
			this.end = end;
			this.time = time;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			RoadMap.Node st = b.getNodeFromString(start.getText());
			RoadMap.Node e = b.getNodeFromString(end.getText());
			int num = Integer.parseInt(this.time.getText().trim());
			int fdigits = num / 60;
			int ldigits = num % 100;
			

			if (e != null && st != null) {
				result = b.findMinTimeAccountingForTraffic(st, e, fdigits * 60 + ldigits);
				changeNodeColor();
				repaint();
			} else {
				oArea.setText("Your landmark or city was not found. Check your spelling!");
			}
		}
	}
}

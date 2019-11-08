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
import javax.swing.JTextField;

public class MapVisualizer extends JPanel {
	public RoadMap b;
	public ArrayList<RoadMap.Node> cities;
	public int w;
	public int h;
	public JTabbedPane tab;
	public static final int radius = 25;

	public MapVisualizer(RoadMap m) {
		this.setLayout(null);
		this.w = 900;
		this.h = 900;
		this.b = m;
		this.cities = m.getAllCities();
		this.tab = new JTabbedPane();
		this.makeTitle();
		this.tripPlanner();
		this.shortRoute();
		this.searcher();
		this.tab.setSize(w / 2, h / 2);
		this.tab.setLocation(200, 100);
		this.add(tab, BorderLayout.SOUTH);
		

	}

	private void searcher() {
		JTextField searcher = this.makeField("Enter in a character and see which city you want!");
		this.tab.addTab("City Searcher", searcher);

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

	@Override
	public void paintComponent(Graphics g) {// Bottom right is Galv

		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		int d = 30;
		double ref = cities.get(3).getLongitude();
		double ref2 = cities.get(4).getLatitude();

		for (RoadMap.Node n : cities) {
			g.setColor(n.getColor());
			n.getLatitude(); // y
			int x = 1300 - (int) (n.getLongitude() * 100 - ref * 100);// x smaller
																	// you are
																	// the
																	// farther
																	// right you
																	// are
			int y = 700 + (int) (ref2 * 30 - n.getLatitude() * 30);
			g.drawOval(x, y, radius, radius);
			g.drawString(n.getName(), x, y);

		}
	}

	public void makeTitle() {
		JLabel titleText = new JLabel("Lone Star Traversal");
		titleText.setLocation(w / 2 - 100, -h / 2 + 50);
		titleText.setSize(w, h);
		titleText.setFont(new Font("Freestyle Script", 1, 30));

		this.add(titleText);
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

	public void shortRoute() {
		GridLayout SRlayout = new GridLayout(5, 0);

		JPanel SRPanel = new JPanel();
		SRPanel.setOpaque(true);
		SRPanel.setBackground(Color.white);
		SRPanel.setSize(200, 200);
		SRPanel.setLayout(SRlayout);

		JLabel startlabel = this.makeLabel("Starting City: ");
		JLabel endlabel = this.makeLabel("Ending City:");

		JTextField start = this.makeField("Enter Your Starting City");
		JTextField end = this.makeField("Enter Your Ending City");

		JButton SR = new JButton("Go!");
		SR.addActionListener(new ButtonListener());

		SRPanel.add(startlabel);
		SRPanel.add(start);
		SRPanel.add(endlabel);
		SRPanel.add(end);
		SRPanel.add(SR);

		tab.addTab("Shortest Route", SRPanel);

	}

	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("Button pressed");
		}
	}

}

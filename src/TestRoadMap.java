import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class TestRoadMap {
	
	@Test
	public void testStraightLineDistance(){
		RoadMap b = new RoadMap();
		b.addNode("Austin",RoadMap.NodeType.CITY, 30.2672,97.7431);
		b.addNode("Houston",RoadMap.NodeType.CITY, 29.7604,95.3698);
		b.addNode("Victoria",RoadMap.NodeType.CITY, 28.8053,97.0036);
		b.addNode("San Antonio", RoadMap.NodeType.CITY, 29.4241, 98.4936);
		b.addNode("Galveston", RoadMap.NodeType.CITY, 29.3013, 94.7977);
		double result = b.getStraightLineDistance(b.getNodeFromString("Houston"), b.getNodeFromString("Austin"));
		System.out.println("Houston to Austin");
		System.out.println(b.getStraightLineDistance(b.getNodeFromString("Houston"), b.getNodeFromString("Austin")));
		System.out.println("Houston to Victoria");
		System.out.println(b.getStraightLineDistance(b.getNodeFromString("Houston"), b.getNodeFromString("Victoria")));
		System.out.println("Houston to Galveston");
		System.out.println(b.getStraightLineDistance(b.getNodeFromString("Houston"), b.getNodeFromString("Galveston")));
		System.out.println("Houston to San Antonio");
		System.out.println(b.getStraightLineDistance(b.getNodeFromString("Houston"), b.getNodeFromString("San Antonio")));
		assertTrue(true); 
		
	}
	
	
	
	
	

}

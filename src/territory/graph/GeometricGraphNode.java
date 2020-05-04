package territory.graph;

import javafx.geometry.Point2D;
import territory.game.sprite.Sprite;

import java.util.*;

public class GeometricGraphNode {
	
	private double x, y;
	
	private List<GeometricGraphNode> neighbors = new ArrayList<>();
	
	public GeometricGraphNode(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void addNeighbor(GeometricGraphNode node){
		if(neighbors.contains(node)){
			return;
		}
		
		neighbors.add(node);
	}

	public void removeNeighbor(GeometricGraphNode neighbor){
		neighbors.remove(neighbor);
	}
	
	public List<GeometricGraphNode> getNeighbors(){
		return neighbors;
	}

	/**
	 * Sort this nodes neighbors by angle
	 */
	public void sortNeighbors(){
		//find the angle that each neighbor makes
		HashMap<GeometricGraphNode, Double> angles = new HashMap<>(neighbors.size());

		for(GeometricGraphNode neighbor : neighbors){
			Point2D vector = new Point2D(x, y).subtract(neighbor.x, neighbor.y);
			angles.put(neighbor, Sprite.rotation(vector));
		}

		Collections.sort(neighbors, Comparator.comparingDouble(angles::get));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString(){
		return String.format("(%.2f, %.2f)", x, y);
	}
}
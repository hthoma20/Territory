package territory.graph;

import javafx.geometry.Point2D;
import territory.game.sprite.Sprite;

import java.util.*;

public class GeometricGraphNode {
	
	private double x, y;

	private String name = "";
	
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
			Point2D vector = new Point2D(neighbor.x, neighbor.y).subtract(this.x, this.y);
			angles.put(neighbor, Sprite.rotation(vector));
		}

		neighbors.sort(Comparator.comparingDouble(angles::get));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setName(char name){
		this.name = Character.toString(name);
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public String toString(){
		return String.format("%s (%.2f, %.2f)", name, x, y);
	}
}
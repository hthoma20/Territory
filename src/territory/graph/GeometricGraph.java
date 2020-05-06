package territory.graph;

import java.util.*;

public class GeometricGraph {

	private List<GeometricGraphNode> nodes = new ArrayList<>();

	public GeometricGraph(){

	}
	
	public GeometricGraph(Collection<GeometricGraphNode> nodes){
		this.nodes = new ArrayList<>(nodes);
	}

	/**
	 * Make a copy of this graph. The copy will have different nodes,
	 * but will be isomorphic to this graph.
	 *
	 * @return a copy of this graph
	 */
	public GeometricGraph copy(){
		//we are making new nodes so keep a mapping so that we can preserve adjacency
		HashMap<GeometricGraphNode, GeometricGraphNode> nodeMapping = new HashMap<>();

		//first create the mapping
		for(GeometricGraphNode node : this.nodes){
			nodeMapping.put(node, new GeometricGraphNode(node.getX(), node.getY()));
		}

		//preserve the neighbors
		for(GeometricGraphNode node : this.nodes){
			GeometricGraphNode newNode = nodeMapping.get(node);

			for(GeometricGraphNode neighbor : node.getNeighbors()){
				newNode.addNeighbor(nodeMapping.get(neighbor));
			}
		}

		//return the new graph
		return new GeometricGraph(nodeMapping.values());
	}
	
	public void addNode(GeometricGraphNode node){
		if(nodes.contains(node)){
			return;
		}
		
		nodes.add(node);
	}
	
	public void removeNode(GeometricGraphNode node){
		for(GeometricGraphNode neighbor : node.getNeighbors()){
			neighbor.removeNeighbor(node);
		}

		nodes.remove(node);
	}

	public void addEdge(GeometricGraphNode node1, GeometricGraphNode node2){
		node1.addNeighbor(node2);
		node2.addNeighbor(node1);
	}
	
	public boolean isConnected(){
		GeometricGraph firstComponent = connectedComponent(nodes.get(0));
		
		//this graph is connected iff the first component is the entire graph
		return firstComponent.nodes.size() == this.nodes.size();
	}

	/**
	 * Create a list of components of this graph, so that each component is connected
	 * and contains only nodes involved in a cycle
	 * @return the connected cyclic components of the this graph
	 */
	public List<GeometricGraph> connectedCyclicComponents(){
		List<GeometricGraph> connectedComponents = this.connectedComponents();
		List<GeometricGraph> connectedCyclicComponents = new ArrayList<>();

		while(connectedComponents.size() > 0){
			GeometricGraph graph = connectedComponents.remove(0);
			GeometricGraph cyclic = graph.cyclicComponent();

			if(graph.isConnected()){
				connectedCyclicComponents.add(graph);
			}
			else{
				connectedComponents.addAll(cyclic.connectedComponents());
			}
		}

		return connectedCyclicComponents;
	}

	/**
	 * @return a subgraph this graph, with only nodes and edges which are part
	 * of a cycle
	 */
	public GeometricGraph cyclicComponent(){
		HashSet<GeometricGraphNode> nodesToKeep = new HashSet<>();
		HashSet<GeometricGraphNode> nodesToRemove = new HashSet<>();


		//try to remove each node. if the graph is still connected, the node is part of a cycle
		for(GeometricGraphNode node : nodes){
			//if it only has one neighbor, it is definitely not part of a cycle
			if(node.getNeighbors().size() < 2){
				nodesToRemove.add(node);
			}

			GeometricGraph copy = this.copy();
			copy.removeNode(node);
			if(copy.isConnected()){
				nodesToKeep.add(node);
			}
			else{
				nodesToRemove.add(node);
			}
		}

		//make the new graph
		GeometricGraph cyclicGraph = new GeometricGraph(nodesToKeep);

		//remove the neighbors we don't want
		for(GeometricGraphNode node : nodesToRemove){
			cyclicGraph.removeNode(node);
		}

		return cyclicGraph;
	}

	/**
	 * @return a list of graphs. This list has the following properties:
	 * 1) It partitions this graph
	 * 2) Each graph is connected
	 * 3) If G1 and G2 are distinct elements of the list and N1 is in G1, N2 is in G2,
	 *		then N1 is not a neighbor of N2
	 */
	public List<GeometricGraph> connectedComponents(){
		HashSet<GeometricGraphNode> unvisited = new HashSet<>(this.nodes);
		
		List<GeometricGraph> components = new ArrayList<>();
		
		//while there are nodes that aren't part of components we've found
		while( !unvisited.isEmpty() ){
			
			//choose an unvisited node
			GeometricGraphNode node = unvisited.iterator().next();
			
			//find its component
			GeometricGraph component = connectedComponent(node);
			
			components.add(component);
			unvisited.removeAll(component.nodes);
		}
		
		return components;
	}
	
	
	/**
	 * Return a subgraph of nodes that are transatively reachable from the given node
	 */
	private GeometricGraph connectedComponent(GeometricGraphNode node){
		HashSet<GeometricGraphNode> component = new HashSet<>();
		
		connectedComponent(node, component);
		
		return new GeometricGraph(component);
	}
	
	/**
	 * Add the given node and all its transitive neighbors to the given set
	 */
	private void connectedComponent(GeometricGraphNode node, Set<GeometricGraphNode> component){
		component.add(node);
		
		for(GeometricGraphNode neighbor : node.getNeighbors()){
			if( !component.contains(neighbor) ){
				connectedComponent(neighbor, component);
			}
		}
	}

	/**
	 * Find the perimeter of this graph. The last node in the list will be neighbors with the first
	 * @return the perimeter of this graph
	 */
	public List<GeometricGraphNode> perimeter(){
		nameNodes();

		//sort neighbors by angle
		for(GeometricGraphNode node : nodes){
			node.sortNeighbors();
		}

		//start at the right-most node
		GeometricGraphNode start = Collections.max(nodes, Comparator.comparingDouble(n -> n.getX()));

		List<GeometricGraphNode> perimeter = new ArrayList<>();
		perimeter.add(start);

		GeometricGraphNode prev = start;
		GeometricGraphNode curr = start.getNeighbors().get(0);

		while(curr != start){
			perimeter.add(curr);

			//go to the neighbor after the one we just came from
			int prevIndex = curr.getNeighbors().indexOf(prev);
			int nextIndex = (prevIndex + 1) % curr.getNeighbors().size();

			prev = curr;
			curr = curr.getNeighbors().get(nextIndex);
		}

		return perimeter;
	}

	public void nameNodes(){
		nodes.sort(Comparator.comparingDouble(n -> n.getX()));

		char name = 'A';
		for(GeometricGraphNode node : nodes){
			node.setName(name++);
		}
	}

	public int size(){
		return nodes.size();
	}
}
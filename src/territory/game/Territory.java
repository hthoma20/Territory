package territory.game;

import territory.game.GameColor;
import territory.game.construction.Post;
import territory.game.construction.Wall;
import territory.graph.GeometricGraph;
import territory.graph.GeometricGraphNode;

import java.io.Serializable;
import java.util.*;

public class Territory implements Copyable<Territory>, Serializable {

    private GameColor color;

    private double[] xPoints;
    private double[] yPoints;

    public Territory(GameColor color, double[] xPoints, double[] yPoints){
        if(xPoints.length != yPoints.length){
            throw new RuntimeException(String.format("Provided %d xPoints, but %d yPoints",
                            xPoints.length, yPoints.length));
        }

        this.xPoints = xPoints;
        this.yPoints = yPoints;

        this.color = color;
    }

    public Territory(Territory src){
        this.xPoints = Arrays.copyOf(src.xPoints, src.xPoints.length);
        this.yPoints = Arrays.copyOf(src.yPoints, src.yPoints.length);
        this.color = src.color;
    }

    @Override
    public Territory copy(){
        return new Territory(this);
    }

    public static TerritoryList fromWalls(List<Wall> walls){
        GeometricGraph graph = initGraph(walls);
        List<GeometricGraph> connectedComponents = graph.connectedComponents();

        TerritoryList territories = new TerritoryList();
        GameColor color = walls.get(0).getColor();

        //each of these cycles maps to a single territory
        for(GeometricGraph component : connectedComponents){
            if(component.size() < 3){
                continue;
            }

            territories.append(fromConnectedComponent(component, color));
        }

        return territories;
    }

    private static TerritoryList fromConnectedComponent(GeometricGraph component, GameColor color){
        return fromPerimeter(component.perimeter(), color);
    }

    private static TerritoryList fromPerimeter(List<GeometricGraphNode> perimeter, GameColor color){
        //base case: the perimeter has less than 3 nodes
        //implies this does not define territory
        if(perimeter.size() < 3){
            return new TerritoryList();
        }

        //first find the first repeated node
        int[] repeatedNodeIndices = repeatedNode(perimeter);

        //base case: there are no repetitions
        //implies that the entire perimeter defines a single territory
        if(repeatedNodeIndices == null){
            return new TerritoryList(fromNodes(perimeter, color));
        }

        //otherwise, there is a repeated node and we must break this into two sub-perimeters:
        //one just ignores everything within the repetition, and the other just includes those
        int firstIndex = repeatedNodeIndices[0];
        int lastIndex = repeatedNodeIndices[1];

        //break the perimeter into sublists, and then recurse
        List<GeometricGraphNode> preBranch = new ArrayList<>(perimeter.subList(0, firstIndex));
        List<GeometricGraphNode> postBranch = perimeter.subList(lastIndex, perimeter.size());
        preBranch.addAll(postBranch);
        List<GeometricGraphNode> branch = perimeter.subList(firstIndex, lastIndex);

        TerritoryList territories = new TerritoryList();
        territories.append(fromPerimeter(preBranch, color));
        territories.append(fromPerimeter(branch, color));

        return territories;
    }

    public static Territory fromNodes(List<GeometricGraphNode> nodes, GameColor color){
        double[] xPoints = new double[nodes.size()];
        double[] yPoints = new double[nodes.size()];

        for(int i = 0; i < nodes.size(); i++){
            GeometricGraphNode node = nodes.get(i);
            xPoints[i] = node.getX();
            yPoints[i] = node.getY();
        }

        return new Territory(color, xPoints, yPoints);
    }

    /**
     * @param nodes the nodes to search for repetitions
     * @return the indices of the first repeated node and its first repetition
     */
    private static int[] repeatedNode(List<GeometricGraphNode> nodes){
        HashMap<GeometricGraphNode, List<Integer>> indices = indices(nodes);

        for(GeometricGraphNode node : nodes){
            List<Integer> nodeIndices = indices.get(node);
            if(nodeIndices.size() > 1){
                return new int[]{nodeIndices.get(0), nodeIndices.get(1)};
            }
        }

        //if we get here, there is no repeated node
        return null;
    }

    /**
     * Find all indices of the given nodes in the given list
     * @param nodes the nodes to find indices of
     * @return a map from node -> list of indices where the node appears in the list
     */
    private static HashMap<GeometricGraphNode, List<Integer>> indices(List<GeometricGraphNode> nodes){
        HashMap<GeometricGraphNode, List<Integer>> indices = new HashMap<>(nodes.size());

        for(int i = 0; i < nodes.size(); i++){
            GeometricGraphNode node = nodes.get(i);
            if(!indices.containsKey(node)){
                indices.put(node, new ArrayList<>());
            }

            indices.get(node).add(i);
        }

        return indices;
    }

    private static GeometricGraph initGraph(List<Wall> walls){
        HashMap<Post, GeometricGraphNode> postMapping = mapPosts(walls);

        GeometricGraph graph = new GeometricGraph(postMapping.values());

        for(Wall wall : walls){
            if(!wall.isComplete()){
                continue;
            }
            graph.addEdge(postMapping.get(wall.getPost1()), postMapping.get(wall.getPost2()));
        }

        return graph;
    }

    private static HashMap<Post, GeometricGraphNode> mapPosts(List<Wall> walls){
        HashSet<Post> posts = new HashSet<>();

        for(Wall wall : walls){
            if(!wall.isComplete()){
                continue;
            }
            posts.add(wall.getPost1());
            posts.add(wall.getPost2());
        }

        HashMap<Post, GeometricGraphNode> mapping = new HashMap<>();

        for(Post post : posts){
            mapping.put(post, new GeometricGraphNode(post.getX(), post.getY()));
        }

        return mapping;
    }

    public double area(){
        /*
         * Implementation of this algorithm:
         * https://www.mathopenref.com/coordpolygonarea2.html
         */

        double area = 0;
        int j = getNumPoints()-1;

        for (int i = 0; i < getNumPoints(); i++){
            area += (xPoints[i]+xPoints[j]) * (yPoints[i]-yPoints[j]);
            j = i;  //j is previous vertex to i
        }

        return area/2;
    }

    public GameColor getColor() {
        return color;
    }

    public double[] getXPoints() {
        return xPoints;
    }

    public double[] getYPoints() {
        return yPoints;
    }

    public int getNumPoints(){
        return xPoints.length;
    }
}

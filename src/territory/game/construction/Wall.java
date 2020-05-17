package territory.game.construction;

import territory.game.*;

import territory.game.action.tick.CheckTerritoryAction;
import territory.game.action.tick.TickAction;
import territory.game.sprite.ImageStore;
import territory.game.sprite.Sprite;
import javafx.geometry.Point2D;

import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Wall implements Tickable, Copyable<Wall>, Indexable, Serializable {
    private GameColor color;

    private int index = -1;

    private Post post1, post2;
    private WallSegment[] segments;

    //whether this wall was complete before this tick
    private boolean wasComplete = false;

    public Wall(GameColor color, Post post1, Post post2){
        this.color = color;

        this.post1 = post1;
        this.post2 = post2;

        initWallSegments();
    }

    public Wall(Wall src){
        this.color = src.color;
        this.index = src.index;
        this.post1 = src.post1;
        this.post2 = src.post2;

        this.segments = new WallSegment[src.segments.length];
        for(int i = 0; i < src.segments.length; i++){
            this.segments[i] = src.segments[i].copy();
        }

        this.wasComplete = src.wasComplete;
    }

    @Override
    public Wall copy() {
        return new Wall(this);
    }

    private void initWallSegments(){

        //find the points on the edges of the posts
        double postRadius = ImageStore.store.imageFor(Post.class, this.color).getWidth()/2;
        Point2D post1Point = new Point2D(post1.getX() + postRadius, post1.getY() + postRadius);
        Point2D post2Point = new Point2D(post2.getX() + postRadius, post2.getY() + postRadius);

        if(post1Point.getY() > post2Point.getY()){
            Point2D temp = post1Point;
            post1Point = post2Point;
            post2Point = temp;
        }

        Point2D normalDistance = post2Point.subtract(post1Point).normalize();
        Point2D p1 = post1Point.add(normalDistance.multiply(postRadius));
        Point2D p2 = post1Point.add(normalDistance.multiply(post1Point.subtract(post2Point).magnitude() - postRadius));

        Point2D distanceVector = p2.subtract(p1);
        double distance = distanceVector.magnitude();

        //compute the number of segments
        double segmentLength = ImageStore.store.imageFor(WallSegment.class, this.color).getWidth();
        int numSegments = (int)(distance/segmentLength);

        //compute spacing
        // there is a space on both side of each segment, so there are numSegments+1 spaces, so
        // numSegments*segmentLength + (numSegments+1)*spacing = distance
        double spacing = (distance - numSegments*segmentLength)/(numSegments+1);

        //create the segments
        this.segments = new WallSegment[numSegments];

        //compute rotation of walls
        double rotation = Sprite.rotation(distanceVector);

        //find the placement of each segment
        //the for loop iterates distance from p1
        double start = spacing;
        double step = spacing + segmentLength;
        double stop = distance - step;
        int index = 0;
        for(double d = start; d <= stop+.001; d += step){

            Point2D distanceFromP1 = normalDistance.multiply(d);
            Point2D segmentPoint = p1.add(distanceFromP1);

            WallSegment wallSegment = new WallSegment(this, segmentPoint.getX(), segmentPoint.getY(), rotation);
            segments[index] = wallSegment;
            index++;
        }

        System.out.printf("Created wall with %d segments", segments.length);
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    public static int getGoldPrice(){
        return 0;
    }

    public WallSegment[] getSegments() {
        return segments;
    }

    public GameColor getColor(){
        return this.color;
    }

    public Post getPost1() {
        return post1;
    }

    public Post getPost2() {
        return post2;
    }

    /**
     * @param post the post to check for
     * @return whether the given post is one of this wall's posts
     */
    public boolean containsPost(Post post){
        return post == post1 || post == post2;
    }

    /**
     * @param p1 the start of the given line segment
     * @param p2 the end of the given line segment
     * @return whether this wall intersects the given line segment
     */
    public boolean intersects(Post p1, Post p2){
        Line2D thisWall = new Line2D.Double(this.post1.getX(), this.post1.getY(), this.post2.getX(), this.post2.getY());
        Line2D newWall = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        return thisWall.intersectsLine(newWall);
    }

    public boolean isComplete() {
        for(WallSegment segment : segments){
            if(!segment.isComplete()){
                return false;
            }
        }

        return true;
    }

    @Override
    public List<TickAction> tick(GameState currentState){
        if(wasComplete){
            return null;
        }

        if(isComplete()){
            wasComplete = true;
            return Arrays.asList(new CheckTerritoryAction(this.color));
        }

        return null;
    }
}

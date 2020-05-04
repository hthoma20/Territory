package territory.game.construction;

import territory.game.Copyable;
import territory.game.GameColor;
import territory.game.Indexable;

import territory.game.Tickable;
import territory.game.action.tick.CheckTerritoryAction;
import territory.game.action.tick.TickAction;
import territory.game.sprite.ImageStore;
import territory.game.sprite.Sprite;
import javafx.geometry.Point2D;

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
    }

    @Override
    public Wall copy() {
        return new Wall(this);
    }

    private void initWallSegments(){
        Point2D p1 = new Point2D(post1.getX(), post1.getY());
        Point2D p2 = new Point2D(post2.getX(), post2.getY());

        //compute distance between the two posts
        Point2D distance = p2.subtract(p1);
        double segmentLength = ImageStore.store.imageFor(WallSegment.class, color).getWidth();

        //round up the number of segments needed
        int numSegments = (int)(distance.magnitude()/segmentLength + 1);

        //create the segments array
        segments = new WallSegment[numSegments];

        //normalize the distance vector for use in the initilization loop
        Point2D normalDistance = distance.normalize();

        //find the rotation of each segment
        double rotation = Sprite.rotation(distance);

        //initialize each segment
        for(int i = 0; i < segments.length; i++){
            Point2D segmentPoint = p1.add(distance.normalize().multiply(segmentLength*i));
            segments[i] = new WallSegment(this, segmentPoint.getX(), segmentPoint.getY(), rotation);
        }
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

    public boolean isComplete() {
        for(WallSegment segment : segments){
            if(!segment.isComplete()){
                return false;
            }
        }

        return true;
    }

    @Override
    public List<TickAction> tick(){
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

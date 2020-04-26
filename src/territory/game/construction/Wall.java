package territory.game.construction;

import territory.game.Copyable;
import territory.game.Indexable;

import territory.game.player.Player;
import territory.game.sprite.ImageStore;
import territory.game.sprite.Sprite;
import javafx.geometry.Point2D;

public class Wall implements Copyable<Wall>, Indexable, BuildProject {
    private Player owner;

    private int index = -1;

    private Post post1, post2;
    private WallSegment[] segments;

    public Wall(Player owner, Post post1, Post post2){
        this.owner = owner;

        this.post1 = post1;
        this.post2 = post2;

        initWallSegments();
    }

    public Wall(Wall src){
        this.owner = src.owner;
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
        double segmentLength = ImageStore.store.imageFor(WallSegment.class, owner.getColor()).getWidth();

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
            segments[i] = new WallSegment(owner, segmentPoint.getX(), segmentPoint.getY(), rotation);
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

    @Override
    public boolean isComplete() {
        for(WallSegment segment : segments){
            if(!segment.isComplete()){
                return false;
            }
        }

        return true;
    }

    @Override
    public BuildSlot getOpenBuildSlot(){
        BuildSlot minSlot = segments[0].getOpenBuildSlot();

        for(int i = 1; i < segments.length; i++){
            BuildSlot slot = segments[i].getOpenBuildSlot();

            if(slot.getUnitCount() < minSlot.getUnitCount()){
                minSlot = slot;
            }
        }

        return minSlot;
    }
}

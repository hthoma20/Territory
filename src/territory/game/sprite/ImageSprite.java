package territory.game.sprite;

import territory.game.GameColor;
import javafx.scene.image.Image;

import javafx.geometry.Point2D;
import java.io.Serializable;

public abstract class ImageSprite extends Sprite implements Serializable {

    public ImageSprite(double x, double y) {
        super(x, y);
    }

    public ImageSprite(ImageSprite src){
        super(src);
    }

    @Override
    public boolean containsPoint(double x, double y) {
        double topX = getTopX();
        double topY = getTopY();
        double width = getWidth();
        double height = getHeight();

        return  (topX <= x && x <= topX + width) &&
                (topY <= y && y <= topY + height);
    }

    @Override
    public Image getImage(){
        return ImageStore.store.imageFor(this, getColor());
    }

    @Override
    public double distanceFrom(double x, double y){
        return new Point2D(this.x, this.y).distance(x, y);
    }

    public abstract GameColor getColor();
}

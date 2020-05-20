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
        Image image = getImage();
        double width = image.getWidth();
        double height = image.getHeight();

        return  (this.x <= x && x <= this.x + width) &&
                (this.y <= y && y <= this.y + height);
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

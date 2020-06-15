package territory.game.sprite;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
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
    public double getWidth(){
        return getImage().getWidth();
    }

    @Override
    public double getHeight(){
        return getImage().getHeight();
    }

    public Image getImage(){
        return ImageStore.store.imageFor(this, getColor());
    }

    @Override
    public void paintOn(GraphicsContext gc){
        gc.save();

        Image image = getImage();

        //rotate the image according to the sprites rotation
        gc.transform(new Affine(new Rotate(rotation, x, y)));

        gc.drawImage(image, getTopX(), getTopY());

        gc.restore();
    }

    @Override
    public void paintHighlightOn(GraphicsContext gc){
        double highlightMargin = 2;
        double highlightOpacity = .2;

        double x = getTopX() - highlightMargin;
        double y = getTopY() - highlightMargin;
        double width = getWidth() + 2*highlightMargin;
        double height = getHeight() + 2*highlightMargin;

        gc.setFill(new Color(0, 1, 0, highlightOpacity));

        gc.fillRect(x, y, width, height);
    }

    @Override
    public double distanceFrom(double x, double y){
        return new Point2D(this.x, this.y).distance(x, y);
    }

    public abstract GameColor getColor();
}

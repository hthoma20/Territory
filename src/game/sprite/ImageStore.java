package game.sprite;

import game.GameColor;
import game.GameState;
import game.construction.Mine;
import game.construction.Post;
import game.construction.Village;
import game.construction.WallSegment;
import game.unit.Builder;
import game.unit.Miner;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Objects;

/**
 * Class to store images by class
 */
public class ImageStore {
    private HashMap<ClassColorPair, Image> imageMap;

    public static final ImageStore store = new ImageStore();

    private ImageStore(){
        this.imageMap = new HashMap<>();

        //Units
        loadImage(Miner.class, GameColor.PURPLE, "units/Miner_purple.png");
        loadImage(Builder.class, GameColor.PURPLE, "units/Builder_purple.png");

        //Villages
        loadImage(Village.class, GameColor.PURPLE, "constructions/Village_purple.png");

        //Posts
        loadImage(Post.class, GameColor.PURPLE, "constructions/Post_purple.png");

        //Walls
        loadImage(WallSegment.class, GameColor.PURPLE, "constructions/Wall_purple.png");

        //Mines
        loadImage(Mine.class, null, "constructions/Mine.png");
    }

    public Image imageFor(Class<? extends Sprite> clazz, GameColor color){
        Image image = imageMap.get(new ClassColorPair(clazz, color));

        if(image == null){
            throw new RuntimeException(
                    String.format("Could not find image for %s %s", color, clazz.getName()));
        }

        return image;
    }

    public Image imageFor(Sprite sprite, GameColor color){
        return imageFor(sprite.getClass(), color);
    }

    private void loadImage(Class<? extends Sprite> clazz, GameColor color, String imagePath){
        this.imageMap.put(new ClassColorPair(clazz, color), new Image(imagePath));
    }

    private static class ClassColorPair{
        Class<? extends Sprite> clazz;
        GameColor color;

        public ClassColorPair(Class<? extends Sprite> clazz, GameColor color){
            this.clazz = clazz;
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassColorPair that = (ClassColorPair) o;
            return Objects.equals(clazz, that.clazz) &&
                    color == that.color;
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, color);
        }
    }
}

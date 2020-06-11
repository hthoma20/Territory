package territory.game.sprite;

import territory.game.GameColor;
import territory.game.construction.Mine;
import territory.game.construction.Post;
import territory.game.construction.Village;
import territory.game.construction.WallSegment;
import territory.game.unit.Builder;
import territory.game.unit.Miner;
import javafx.scene.image.Image;
import territory.game.unit.Soldier;

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
        loadImage(Miner.class, GameColor.GREEN, "units/Miner_green.png");
        loadImage(Miner.class, GameColor.BLUE, "units/Miner_blue.png");
        loadImage(Builder.class, GameColor.PURPLE, "units/Builder_purple.png");
        loadImage(Builder.class, GameColor.GREEN, "units/Builder_green.png");
        loadImage(Builder.class, GameColor.BLUE, "units/Builder_blue.png");
        loadImage(Soldier.class, GameColor.PURPLE, "units/Soldier_purple.png");
        loadImage(Soldier.class, GameColor.GREEN, "units/Soldier_green.png");
        loadImage(Soldier.class, GameColor.BLUE, "units/Soldier_blue.png");

        //Villages
        loadImage(Village.class, GameColor.PURPLE, "constructions/Village_purple.png");
        loadImage(Village.class, GameColor.GREEN, "constructions/Village_green.png");
        loadImage(Village.class, GameColor.BLUE, "constructions/Village_blue.png");

        //Posts
        loadImage(Post.class, GameColor.PURPLE, "constructions/Post_purple.png");
        loadImage(Post.class, GameColor.GREEN, "constructions/Post_green.png");
        loadImage(Post.class, GameColor.BLUE, "constructions/Post_blue.png");

        //Walls
        loadImage(WallSegment.class, GameColor.PURPLE, "constructions/Wall.png");
        loadImage(WallSegment.class, GameColor.GREEN, "constructions/Wall.png");
        loadImage(WallSegment.class, GameColor.BLUE, "constructions/Wall.png");

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

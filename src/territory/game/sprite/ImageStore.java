package territory.game.sprite;

import territory.game.GameColor;
import territory.game.construction.*;
import territory.game.unit.Builder;
import territory.game.unit.Lumberjack;
import territory.game.unit.Miner;
import javafx.scene.image.Image;
import territory.game.unit.Soldier;

import java.util.HashMap;
import java.util.Objects;

/**
 * Class to store images by class
 */
public class ImageStore {
    private HashMap<String, Image> imageMap;

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
        loadImage(Lumberjack.class, GameColor.PURPLE, "units/Lumberjack_purple.png");

        //Villages
        loadImage(Village.class, GameColor.PURPLE, "constructions/Village_purple.png");
        loadImage(Village.class, GameColor.GREEN, "constructions/Village_green.png");
        loadImage(Village.class, GameColor.BLUE, "constructions/Village_blue.png");

        //Village expansions
        loadImage("Well", "constructions/Well.png");
        loadImage("Barracks", "constructions/Barracks.png");

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

        //Trees
        loadImage(Tree.class, null, "constructions/Tree.png");
    }

    public Image imageFor(String key){
        Image image = imageMap.get(key);

        if(image == null){
            throw new RuntimeException(
                    String.format("Could not find image for %s", key));
        }

        return image;
    }

    public Image imageFor(Class<? extends Sprite> clazz, GameColor color){
        Image image = imageMap.get(keyFor(clazz, color));

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
        this.imageMap.put(keyFor(clazz, color), new Image(imagePath));
    }

    private void loadImage(String key, String imagePath){
        this.imageMap.put(key, new Image(imagePath));
    }

    public static String keyFor(Class<? extends Sprite> clazz, GameColor color){
        return String.format("$%s#%s", clazz.getSimpleName(), color);
    }
}

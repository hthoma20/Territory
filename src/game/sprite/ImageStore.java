package game.sprite;

import game.GameColor;
import game.GameState;
import game.construction.Village;
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

        loadImage(Miner.class, GameColor.PURPLE, "units/Miner_purple.png");
        loadImage(Village.class, GameColor.PURPLE, "constructions/Village_purple.png");
    }

    public Image imageFor(Sprite sprite, GameColor color){
        return imageMap.get(new ClassColorPair(sprite.getClass(), color));
    }

    private void loadImage(Class clazz, GameColor color, String imagePath){
        this.imageMap.put(new ClassColorPair(clazz, color), new Image(imagePath));
    }

    private static class ClassColorPair{
        Class clazz;
        GameColor color;

        public ClassColorPair(Class clazz, GameColor color){
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

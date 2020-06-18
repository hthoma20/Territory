package territory.util;

public class GlobalConstants {

    //GlobalConstants constants
    private static final boolean TESTING_MODE = true;

    //JoinController constants
    public static final boolean AUTO_START = TESTING_MODE;
    public static final boolean FORCE_VERSION_FAILURE = false;

    //GameJoiner constants
    public static final boolean ADD_COMPUTER_PLAYER = false;

    //Inventory constants
    public static final int INITIAL_GOLD = TESTING_MODE ? 100000 : 100;
    public static final int INITIAL_STONE = TESTING_MODE ? 100000 : 0;
    public static final int INITIAL_WOOD = TESTING_MODE ? 10000 : 0;

    //Buildable constants
    public static final int BUILDABLE_STONE_NEEDED = TESTING_MODE ? 0 : 100;

    //LocalGame constants
    public static final int TERRITORY_NEEDED = 25000;
}

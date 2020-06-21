package territory.util;

public class GlobalConstants {

    //GlobalConstants constants
    private static final boolean TESTING_MODE = false;

    //JoinController constants
    public static final boolean AUTO_START = TESTING_MODE;
    public static final boolean FORCE_VERSION_FAILURE = false;

    //GameJoiner constants
    public static final boolean ADD_COMPUTER_PLAYER = false;

    //GUIPlayer constants
    public static final boolean TAKE_INITIAL_ACTIONS = false;

    //Inventory constants
    public static final int INITIAL_GOLD = TESTING_MODE ? 100000 : 100;
    public static final int INITIAL_STONE = TESTING_MODE ? 100000 : 0;
    public static final int INITIAL_WOOD = TESTING_MODE ? 10000 : 0;

    //Buildable constants
    public static final int BUILDABLE_STONE_NEEDED = TESTING_MODE ? 0 : 100;

    //LocalGame constants
    public static final int TERRITORY_NEEDED = Integer.MAX_VALUE;

    //Village constants
    public static final int INITIAL_POPULATION = TESTING_MODE ? 900 : 2;
    public static final int MAX_POPULATION = TESTING_MODE ? 1000 : 20;
    public static final double POPULATION_GROWTH_RATE = TESTING_MODE ? 1 : .002;
    public static final int TICKS_PER_GOLD = 200;
    public static final int TICKS_PER_GOLD_WITH_TRADING_POST = 100;

    //Price constants
    public static final int VILLAGE_GOLD = 10;
    public static final int WORK_SHOP_WOOD = 100;
    public static final int BARRACKS_WOOD = 100;
    public static final int TRADING_POST_WOOD = 100;
    public static final int BOOTS_BENCH_WOOD = 50;
    public static final int BOOTS_ITEM_WOOD = 5;
    public static final int ARMOR_BENCH_WOOD = 50;
    public static final int ARMOR_ITEM_WOOD = 5;

    public static final int MINER_GOLD = 5;
    public static final int BUILDER_GOLD = 10;
    public static final int LUMBERJACK_GOLD = 5;
    public static final int SOLDIER_GOLD = 20;

    //Unit constants
    public static final int DEFAULT_HEALTH = 10;
    public static final double DEFAULT_RANGE = 2;
    public static final double DEFAULT_SPEED = 1;
    public static final int ARMOR_HEALTH = 15;

    //Miner constants
    public static final double DEFAULT_MINE_PROBABILITY = .05;
    public static final double DEFAULT_MINER_SPEED = 1;
    public static final double BOOTS_MINER_SPEED = 1.5;

    //Builder constants
    public static final double DEFAULT_BUILD_PROBABILITY = .05;
    public static final double DEFAULT_BUILDER_SPEED = .85;
    public static final double BOOTS_BUILDER_SPEED = 1.25;

    //Lumberjack constants
    public static final double DEFAULT_CHOP_PROBABILITY = .05;
    public static final int DEFAULT_CHOP_STRENGTH = 2;
    public static final double DEFAULT_LUMBERJACK_SPEED = 1;
    public static final double BOOTS_LUMBERJACK_SPEED = 1.5;

    //Soldier constants
    public static final double DEFAULT_ATTACK_PROBABILITY = .05;
    public static final int DEFAULT_ATTACK_STRENGTH = 2;
    public static final double DEFAULT_SOLDIER_SPEED = .75;
    public static final double BOOTS_SOLDIER_SPEED = 1.1;

}

package territory.game.target;

import javafx.geometry.Point2D;
import territory.game.*;
import territory.game.action.tick.TickAction;
import territory.game.unit.Soldier;
import territory.game.unit.Unit;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class PatrolArea implements Tickable, Copyable<PatrolArea>, Serializable {
    private Map<Soldier, Target> soldierTargets = new HashMap<>();

    //color of the troops patrolling this area
    private GameColor color;

    //the center point of this patrol area
    private double x, y;
    private double radius;

    public PatrolArea(GameColor color, double x, double y, double radius){
        this.color = color;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public PatrolArea(PatrolArea src){
        this.soldierTargets = new HashMap<>(src.soldierTargets);

        this.color = src.color;

        this.x = src.x;
        this.y = src.y;
        this.radius = src.radius;
    }

    @Override
    public PatrolArea copy(){
        return new PatrolArea(this);
    }

    public void addSoldier(Soldier soldier){
        if(soldier.getColor() != this.color){
            throw new RuntimeException("Soldier being assigned to patrol area with wrong color");
        }

        soldierTargets.put(soldier, randomTarget());
    }

    public void removeSoldier(Soldier soldier){
        soldierTargets.remove(soldier);
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getRadius() {
        return radius;
    }

    /**
     * Call to register that the given soldier has reached their target
     * If it was a random target, give it a new one, otherwise do nothing
     * @param soldier the soldier that has reached their target
     */
    public void reachedTarget(Soldier soldier){
        Target target = soldierTargets.get(soldier);

        if(target instanceof PointTarget){
            soldierTargets.put(soldier, randomTarget());
        }
    }

    /**
     * @return a random point within this patrol area
     */
    private PointTarget randomTarget(){
        double theta = RNG.randDouble(2 * Math.PI);

        double dx = radius*Math.cos(theta);
        double dy = radius*Math.sin(theta);

        return new PointTarget(getX()+dx, getY()+dy);
    }

    /**
     * @param soldier the soldier for which to find the target
     * @return the target that the given soldier should target
     */
    public Target getTarget(Soldier soldier){
        return soldierTargets.get(soldier);
    }

    public Set<Soldier> getSoldiers(){
        return soldierTargets.keySet();
    }

    @Override
    public List<TickAction> tick(GameState currentState) {

        List<Unit> enemies = findEnemyUnitsInArea(currentState);

        if(enemies.isEmpty()){
            assignSoldiersToRandomSpots();
        }
        else{
            assignSoldiersToEnemies(enemies);
        }


        return null;
    }

    /**
     * For each soldier in the patrol area, if they do not have a random target,
     * give them one
     */
    private void assignSoldiersToRandomSpots(){
        for(Soldier soldier : soldierTargets.keySet()){
            if( !(soldierTargets.get(soldier) instanceof PointTarget) ){
                soldierTargets.put(soldier, randomTarget());
            }
        }
    }

    /**
     * For each soldier in this patrol area,
     * determine which enemy unit the soldier should target
    */
    private void assignSoldiersToEnemies(List<Unit> enemiesInArea){
        //a map from enemy to how many soldiers are targeting them
        HashMap<Unit, Integer> enemyAttackers = new HashMap<>(enemiesInArea.size());

        int groupSize = getSoldiers().size() / enemiesInArea.size() + 1;

        for(Soldier soldier : getSoldiers()){

            Unit nearestEnemy = nearestEnemy(soldier, enemiesInArea);
            int numAttackers = enemyAttackers.getOrDefault(nearestEnemy, 0);

            Unit targetEnemy = null;

            //if theres room in the group to attack the nearest enemy, join the group
            if(numAttackers < groupSize){
                targetEnemy = nearestEnemy;

            }

            //otherwise target the enemy with the least soldiers
            else{
                targetEnemy = Collections.min(enemyAttackers.keySet(), Comparator.comparing(enemyAttackers::get));
            }

            soldierTargets.put(soldier, targetEnemy);
            enemyAttackers.put(targetEnemy, numAttackers+1);
        }
    }

    /**
     * Find the enemy nearest to the soldier
     * @param soldier the soldier to check
     * @param enemies the enemies to check
     * @return the enemy in enemies nearest to soldier
     */
    private Unit nearestEnemy(Soldier soldier, List<Unit> enemies){
        Point2D soldierPoint = new Point2D(soldier.getX(), soldier.getY());

        return Collections.min(enemies, Comparator.comparing( enemy ->
            soldierPoint.distance(enemy.getX(), enemy.getY())
        ));
    }

    /**
     * Find all the enemy units that fall in this patrol area's area of patrol
     * @param state the state to examine
     * @return all enemy units in this patrol area
     */
    private List<Unit> findEnemyUnitsInArea(GameState state){
        ArrayList<Unit> allEnemies = new ArrayList<>();

        for(GameColor color : GameColor.values()){
            if(color == this.color){
                continue;
            }
            allEnemies.addAll(state.getPlayerInventory(color).getUnits());
        }

        List<Unit> enemiesInArea = allEnemies.stream().filter(this::unitInArea).collect(Collectors.toList());
        return enemiesInArea;
    }

    /**
     * @param unit the unit to check
     * @return whether the given unit is in this patrol area
     */
    private boolean unitInArea(Unit unit){

        double distance = new Point2D(x, y).distance(unit.getX(), unit.getY());

        return distance <= radius;
    }
}

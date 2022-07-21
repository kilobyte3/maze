package src.mapGenerateAlgorithm;

import src.MazeLogic;

/**
 * @author Kis Balázs
 */
abstract public class MapGenerateAlgorithm
{
    /**
     * generálás elvégzése
     *
     * @param map               - terrárium
     * @param functionOnRepaint - függvény ami lefut újrarajzoláskor
     */
    public abstract void doIt(MazeLogic.labirynthMapElements[][] map, Runnable functionOnRepaint);

    /**
     * algoritmus neve
     */
    public abstract String getName();
}
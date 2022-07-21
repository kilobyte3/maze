package src;

/**
 * egy elágazás-pontot ír le.
 *
 * @author Kis Balázs kilobyte@freemail.hu
 */
final public class PathPoint
{
    private final int x, y;

    /**
     * konstruktor
     */
    PathPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * x koordináta lekérdezése
     */
    public int getX()
    {
        return x;
    }

    /**
     * y koordináta lekérdezése
     */
    public int getY()
    {
        return y;
    }
}
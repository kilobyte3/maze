package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import src.mapGenerateAlgorithm.MapGenerateAlgorithm;

/**
 * labirintus tárolás, generálási és kiútkeresési logika
 *
 * @author Kis Balázs kilobyte@freemail.hu
 */
final public class MazeLogic
{
    public enum mazeGenerateStateTypes { NONE, MAP, SOLUTION }
    public enum labirynthMapElements { NONE, WALL, START, FINISH, VISITED, SOLUTIONPATH }
    private enum directionsWhileSeekingForExit { UP, RIGHT, LEFT, DOWN }
    public static final boolean DEBUG_SCREENSHOOT = !true; // ha aktiv, akkor minden rajzolás után screenshot
    private mazeGenerateStateTypes mazeGenerateState;
    private labirynthMapElements[][] map = null;
    final private ArrayList<PathPoint> nodeStack; // amikor kiútkereséskor több választási lehetőségünk van, akkor ide jegyezzük fel az a pontot, hogy vissza tudjunk térni
    private int mapW, mapH;
    private int screenShotIndex;
    private final Runnable functionOnRepaint;

    /**
     * konstruktor
     *
     * @param functionOnRepaint - függvény az újrarajzoláshoz
     */
    public MazeLogic(Runnable functionOnRepaint)
    {
        this.functionOnRepaint = functionOnRepaint;
        setMazeGenerateSateNONE();
        nodeStack = new ArrayList<>();
    }

    /**
     * labirintus generálás státusza: nincs
     */
    public void setMazeGenerateSateNONE()
    {
        mazeGenerateState = mazeGenerateStateTypes.NONE;
    }

    /**
     * labirintus generálás státusza: térkép
     */
    public void setMazeGenerateSateMAP()
    {
        mazeGenerateState = mazeGenerateStateTypes.MAP;
    }

    /**
     * labirintus generálás státusza: megoldás
     */
    public void setMazeGenerateSateSOLUTION()
    {
        mazeGenerateState = mazeGenerateStateTypes.SOLUTION;
    }

    /**
     * labirintus generálás státusza
     */
    mazeGenerateStateTypes getMazeGenerateState()
    {
        return mazeGenerateState;
    }

    /**
     * terrárium generálása
     *
     * @param mapGenerateAlgorithm - algoritmus
     * @param width                - szélesség
     * @param height               - magasság
     */
    public void generate(MapGenerateAlgorithm mapGenerateAlgorithm, int width, int height)
    {

        map = new labirynthMapElements[height][width];
        mapW = width;
        mapH = height;

        screenShotIndex = 0;

        // pálya letisztitása, szélek befalazása
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                map[i][j] = labirynthMapElements.NONE;
                if (i == 0 || j == 0 || i == height-1 || j == width-1)
                {
                    map[i][j] = labirynthMapElements.WALL;
                }
                if (i == 0 || j == 0 || i == height-1 || j == width-1)
                {
                    map[i][j] = labirynthMapElements.WALL; // a széleket is befalazzuk
                }
            }
        }
        mapGenerateAlgorithm.doIt(map, functionOnRepaint);
        map[1][1] = labirynthMapElements.START;
        map[1][2] = labirynthMapElements.NONE;
        map[2][1] = labirynthMapElements.NONE;
        map[height-1-1][width-1-1] = labirynthMapElements.FINISH;
    }

    /**
     * ha egy x, y pont "látogatott", akkor "megoldás"-ra jelöljük
     *
     * @param x - x
     * @param y - y
     */
    private void setPointToSolutionIfVisited(int x, int y)
    {
        if (map[y][x] == labirynthMapElements.VISITED)
        {
            map[y][x] = labirynthMapElements.SOLUTIONPATH;
        }
    }

    /**
     * vizszintes vagy függőleges út "látogatott"-ra jelölése
     *
     * @param x1    - x1
     * @param y1    - y1
     * @param prevX - előző x
     * @param prevY - előző Y
     */
    public void drawSolutionLine(int x1, int y1, int prevX, int prevY)
    {
        int distanceX = prevX - x1;
        int distanceY = prevY - y1;
        if (distanceX > 0)
        {
            for(int i = 0; i < distanceX; i++)
            {
                setPointToSolutionIfVisited(x1+i, y1);
            }
        }
        if (distanceX < 0)
        {
            for(int i = 0; i < Math.abs(distanceX); i++)
            {
                setPointToSolutionIfVisited(x1-i, y1);
            }
        }
        if (distanceY > 0)
        {
            for(int i = 0; i < distanceY; i++)
            {
                setPointToSolutionIfVisited(x1, y1+i);
            }
        }
        if (distanceY < 0)
        {
            for(int i = 0; i < Math.abs(distanceY); i++)
            {
                setPointToSolutionIfVisited(x1, y1-i);
            }
        }
    }

    /**
     * true, ha már a finishben vagyunk
     */
    private boolean hasFinishBeenReached(int x, int y)
    {
        if (map[y][x] == labirynthMapElements.FINISH)
        {
            nodeStack.add(new PathPoint(x,y));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * tudunk balra vagy jobbra lépni?
     */
    private boolean checkHorizontally(int x, int y)
    {
        if (map[y][x-1] != labirynthMapElements.WALL && map[y][x-1] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.LEFT))
            {
                return true;
            }
        }
        if (map[y][x+1] != labirynthMapElements.WALL && map[y][x+1] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.RIGHT))
            {
                return true;
            }
        }
        if (hasFinishBeenReached(x, y))
        {
            return true;
        }
        if (map[y][x] != labirynthMapElements.START && map[y][x] != labirynthMapElements.FINISH)
        {
            map[y][x] = labirynthMapElements.VISITED;
        }
        functionOnRepaint.run();
        return false;
    }

    /**
     * tudunk fel/le lépni?
     */
    private boolean checkVertically(int x, int y)
    {
        if (map[y-1][x] != labirynthMapElements.WALL && map[y-1][x] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.UP))
            {
                return true;
            }
        }
        if (map[y+1][x] != labirynthMapElements.WALL && map[y+1][x] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.DOWN))
            {
                return true;
            }
        }
        if (hasFinishBeenReached(x, y))
        {
            return true;
        }
        if (map[y][x] != labirynthMapElements.START && map[y][x] != labirynthMapElements.FINISH)
        {
            map[y][x] = labirynthMapElements.VISITED;
        }
        functionOnRepaint.run();
        return false;
    }

    /**
     * x, y pontról direction irányba haladva egy egyenes út megtétele ütközőig.
     */
    private boolean goStraight(int x, int y, directionsWhileSeekingForExit direction)
    {
        if (hasFinishBeenReached(x, y))
        {
            return true;
        }
        nodeStack.add(new PathPoint(x,y));
        if (map[y][x] != labirynthMapElements.START && map[y][x] != labirynthMapElements.FINISH)
        {
            map[y][x] = labirynthMapElements.VISITED;
        }
        switch(direction)
        {
            case UP :
                do
                {
                    y--;
                    if (checkHorizontally(x, y))
                    {
                        return true;
                    }
                } while(map[y-1][x] != labirynthMapElements.WALL && map[y-1][x] != labirynthMapElements.VISITED);
                break;
            case DOWN :
                do
                {
                    y++;
                    if (checkHorizontally(x, y))
                    {
                        return true;
                    }
                } while(map[y+1][x] != labirynthMapElements.WALL && map[y+1][x] != labirynthMapElements.VISITED);
                break;
            case LEFT :
                do
                {
                    x--;
                    if (checkVertically(x, y))
                    {
                        return true;
                    }
                } while(map[y][x-1] != labirynthMapElements.WALL && map[y][x-1] != labirynthMapElements.VISITED);
                break;
            case RIGHT :
                do
                {
                    x++;
                    if (checkVertically(x, y))
                    {
                        return true;
                    }
                } while(map[y][x+1] != labirynthMapElements.WALL && map[y][x+1] != labirynthMapElements.VISITED);
                break;
        }
        nodeStack.remove(nodeStack.size()-1);
        return false;
    }

    /**
     * csomópontok
     */
    public ArrayList<PathPoint> getNodeStack()
    {
        return nodeStack;
    }

    /**
     * elkezdjük keresni az útvonalat x,y koordinátákból elindulva
     */
    public boolean pathFind(int x, int y)
    {
        nodeStack.clear();
        if (map[y][x+1] != labirynthMapElements.WALL && map[y][x+1] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.RIGHT))
            {
                return true;
            }
        }
        if (map[y][x-1] != labirynthMapElements.WALL && map[y][x-1] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.LEFT))
            {
                return true;
            }
        }
        if (map[y-1][x] != labirynthMapElements.WALL && map[y-1][x] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.UP))
            {
                return true;
            }
        }
        if (map[y+1][x] != labirynthMapElements.WALL && map[y+1][x] != labirynthMapElements.VISITED)
        {
            if (goStraight(x, y, directionsWhileSeekingForExit.DOWN))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * pálya kirajzolása
     */
    public void paintMazeItself(Graphics gContext, boolean withNodes, Rectangle bounds, int size)
    {
        if (map != null)
        {
            for(int i = 0; i < mapH; i++)
            {
                for(int j = 0; j < mapW; j++)
                {
                    switch(map[i][j])
                    {
                        case NONE :
                            gContext.setColor(Color.WHITE);
                            break;
                        case WALL :
                            gContext.setColor(Color.BLACK);
                            break;
                        case START :
                            gContext.setColor(Color.RED);
                            break;
                        case FINISH :
                            gContext.setColor(Color.GREEN);
                            break;
                        case VISITED :
                            gContext.setColor(Color.GRAY);
                            break;
                        case SOLUTIONPATH :
                            gContext.setColor(Color.ORANGE);
                            break;
                    }
                    gContext.fillRect(j*size+10, 110+i*size, size,size);
                }
            }
        }

        if (withNodes)
        {
            gContext.setColor(Color.PINK);
            for(PathPoint element : nodeStack)
            {
                gContext.fillRect(element.getX()*size+10, 110+element.getY()*size, size,size);
            }
        }

        if (MazeLogic.DEBUG_SCREENSHOOT)
        {
            String index = Integer.toString(screenShotIndex);
            if (index.length() == 1) { index = "000"+index; }
            if (index.length() == 2) { index = "00"+index; }
            if (index.length() == 3) { index = "0"+index; }
            try
            {
                ScreenImage.writeImage(ScreenImage.createImage(bounds), "Scr"+index+".gif");
            }
            catch(Exception ex) { }
            screenShotIndex++;
        }
    }

    /**
     * terrárium letisztitása
     */
    public void clear()
    {
        map = null;
        nodeStack.clear();
    }
}
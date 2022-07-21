package src.mapGenerateAlgorithm;

import java.util.Random;
import src.MazeLogic;

/**
 * @author Kis Balázs
 */
final public class Random_ extends MapGenerateAlgorithm
{
    @Override
    public void doIt(MazeLogic.labirynthMapElements[][] map, Runnable functionOnRepaint)
    {
        for(int c = 0; c < (Math.ceil(map.length / 2)-1) * (Math.ceil(map[0].length / 2)-1); c++)
        {
            int ni = new Random().nextInt(map.length / 2) * 2;
            int nj = new Random().nextInt(map[0].length / 2) * 2;
            if (map[ni][nj] == src.MazeLogic.labirynthMapElements.WALL)
            {
                c--;
                continue;
            }
            map[ni][nj] = src.MazeLogic.labirynthMapElements.WALL;
            switch(new Random().nextInt(3))
            {
                case 0 : ni++; break;
                case 1 : nj++; break;
                case 2 : ni--; break;
                case 3 : nj--; break;
            }
            if (ni >= 0 && nj >= 0 && ni < map.length && nj < map[0].length)
            {
                map[ni][nj] = src.MazeLogic.labirynthMapElements.WALL;
            }
            functionOnRepaint.run();
        }
    }

    @Override
    public String getName()
    {
        return "Random";
    }
}
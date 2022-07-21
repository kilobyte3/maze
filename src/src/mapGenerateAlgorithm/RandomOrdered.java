package src.mapGenerateAlgorithm;

import java.util.Random;
import src.MazeLogic;

/**
 * @author Kis Balázs
 */
final public class RandomOrdered extends MapGenerateAlgorithm
{
    @Override
    public void doIt(MazeLogic.labirynthMapElements[][] map, Runnable functionOnRepaint)
    {
        for(int i = 1; i < map.length-1; i++)
        {
            for(int j = 1; j < map[0].length-1; j++)
            {
                if(i % 2 == 0 && j % 2 == 0)
                {
                    map[i][j] = src.MazeLogic.labirynthMapElements.WALL; // minden második korrdinátán egy fal
                }
                else
                {
                    map[i][j] = src.MazeLogic.labirynthMapElements.NONE;
                }
            }
        }

        for(int i = 0; i < map.length / 2; i++)
        {
            for(int j = 0; j < map[0].length / 2; j++)
            {
                int ni = (i*2)+0;
                int nj = (j*2)+0;
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
    }

    @Override
    public String getName()
    {
        return "Random, ordered";
    }
}
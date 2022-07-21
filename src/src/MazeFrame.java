package src;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import src.mapGenerateAlgorithm.*;

/**
 * applikáció GUI
 *
 * @author Kis Balázs kilobyte@freemail.hu
 */
final public class MazeFrame extends JFrame
{
    private static final int F_WIDTH  = 490;
    private static final int F_HEIGHT = 590;

    private final MazeLogic maze;
    private final JTextField tx1, tx2;
    private final JButton button;
    private final JCheckBox cb1;
    private final JLabel lb1;
    private final JComboBox cob1, cob2;
    private Graphics gr;

    /**
     * konstruktor és futtatás
     */
    public MazeFrame()
    {
        maze = new MazeLogic(new Runnable() {
            @Override
            public void run()
            {
                renderMazeAndWait();
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        getContentPane().setLayout(null);

        cob1 = new JComboBox(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" });
        cob1.setSelectedIndex(10-1);
        cob1.setBounds(280, 25, 50, 20);
        cob1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        });
        getContentPane().add(cob1);

        cob2 = new JComboBox(new String[] { new RandomOrdered().getName(), new Random_().getName()});
        cob2.setSelectedIndex(0);
        cob2.setBounds(280, 50, 160, 20);
        cob2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        });
        getContentPane().add(cob2);

        JLabel l5 = new JLabel("Generate:");
        l5.setBounds(220,25, 125,75);
        getContentPane().add(l5);

        JLabel l4 = new JLabel("Pixel size:");
        l4.setBounds(220,25, 100,25);
        getContentPane().add(l4);

        JLabel l1 = new JLabel("Maze width:");
        l1.setBounds(0,0, 100,25);
        getContentPane().add(l1);

        tx1 = new JTextField(20);
        tx1.setText("47");
        tx1.setBounds(100,0, 100,25);
        getContentPane().add(tx1);

        JLabel l2 = new JLabel("Maze height:");
        l2.setBounds(0,25, 100,25);
        getContentPane().add(l2);

        tx2 = new JTextField(20);
        tx2.setText("47");
        tx2.setBounds(100,25, 100,25);
        getContentPane().add(tx2);

        button = new JButton("Generate");
        button.setBounds(0,55, 105,25);
        getContentPane().add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                onButtonClick();
            }
        });

        JLabel l3 = new JLabel("Branches");
        l3.setBounds(120,50, 100,30);
        getContentPane().add(l3);

        cb1 = new JCheckBox();
        cb1.setBounds(190,50, 20,20);
        cb1.setSelected(true);
        getContentPane().add(cb1);
        cb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
            }
        });

        lb1 = new JLabel("");
        lb1.setBounds(250,0, 100,25);
        getContentPane().add(lb1);
        lb1.setText("");

        Dimension Screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((Screen.width - F_WIDTH) / 2, (Screen.height - F_HEIGHT) / 2, F_WIDTH, F_HEIGHT);
        setVisible(true);
    }

    /**
     * labirintus renderelés, és várakozás
     */
    private void renderMazeAndWait()
    {
        if (gr == null)
        {
            gr = getGraphics();
        }
        paintMaze(gr);
        /*try
        {
            Thread.sleep(1);
        }
        catch(InterruptedException ex) { }*/
    }

    /**
     * állapottól függően labirintusgenerálás, kiútkeresés....
     */
    private void onButtonClick()
    {
        switch(maze.getMazeGenerateState())
        {
            case NONE :
                maze.setMazeGenerateSateMAP();
                final MapGenerateAlgorithm mapGenerateAlgorithm;
                switch(cob2.getSelectedIndex())
                {
                    case 0 :
                        mapGenerateAlgorithm = new RandomOrdered();
                        break;
                    case 1 :
                        mapGenerateAlgorithm = new Random_();
                        break;
                    default :
                        throw new RuntimeException("Unhandled algorithm.");
                }
                maze.generate(mapGenerateAlgorithm, Integer.parseInt(tx1.getText()), Integer.parseInt(tx2.getText()));
                button.setText("Solution");
                tx1.setEnabled(false);
                tx2.setEnabled(false);
                repaint();
                break;
            case MAP :
                maze.setMazeGenerateSateSOLUTION();
                if (maze.pathFind(1,1))
                {
                    lb1.setText("There is solution!");
                    int prevX = -1;
                    int prevY = -1;
                    for(PathPoint element : maze.getNodeStack())
                    {
                        if (prevX == -1 && prevY == -1)
                        {
                            prevX = element.getX();
                            prevY = element.getY();
                            continue;
                        }
                        maze.drawSolutionLine(element.getX(), element.getY(), prevX, prevY);
                        prevX = element.getX();
                        prevY = element.getY();
                        renderMazeAndWait();
                    }
                    repaint();
                }
                else
                {
                    lb1.setText("No solution");
                }
                button.setText("Again!");
                tx1.setEnabled(true);
                tx2.setEnabled(true);
                break;
            case SOLUTION :
                maze.clear();
                maze.setMazeGenerateSateNONE();
                repaint();
                break;
        }
    }

    /**
     * paint
     *
     * @param gContext - gcontext
     */
    @Override
    public void paint(Graphics gContext)
    {
        super.paint(gContext);
        paintMaze(gContext);
    }

    /**
     * újrafestés
     *
     * @param gContext - gcontext
     */
    private void paintMaze(Graphics gContext)
    {
        maze.paintMazeItself(gContext, cb1.isSelected(), this.getBounds(), Integer.parseInt(cob1.getSelectedItem().toString()));
    }
}
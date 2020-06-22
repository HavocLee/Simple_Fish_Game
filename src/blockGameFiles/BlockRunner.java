package blockGameFiles;

import javax.swing.*;
import java.awt.*;

public class BlockRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
    {
        public void run()
        {
            runGui();
        }
    });
    }

    public static void runGui()
    {
        JFrame frame = new JFrame("Fish Game");
        Container window = frame.getContentPane();
        //window.setLayout(null);
        window.setBackground(Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Fish Game");
        frame.setLocation(100,50);
        frame.setSize(600,600);
        frame.setResizable(false);

        frame.add(new Fish());
        frame.pack();
        frame.setVisible(true);

    }
}

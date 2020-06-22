package blockGameFiles;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;



public class Fish extends JPanel implements Runnable, KeyListener, ActionListener
{
    private JTextField field;
    private Thread thread;
    private BufferedImage imgDown;
    private BufferedImage imgLeft;
    private BufferedImage imgRight;
    private BufferedImage imgUp;
    private BufferedImage orientedImg;
    private BufferedImage hook;
    private BufferedImage hook1;
    private BufferedImage shark;
    private JButton startBtn;
    private JButton exitBtn;
    private JLabel info;
    private boolean isGameOver;
    private boolean isFirstTime;

    private boolean gameStarted;
    private int x;
    private int fishX;
    private int fishY;
    private int fishVx;
    private int fishVy;
    private int levelX; // 100 by 20
    private int levelY;
    private int level;
    private int[][] fishPos;
    private ArrayList<Double> times;
    private long startTime;
    private double elapsedTime;
    private double bestTime;
    private String timeString;


    public Fish()
    {
        setWindow();
        initThread();
        setFocusable(true);
        addKeyListener(this);
        orientedImg = imgRight;
        gameStarted = false;
        isGameOver = false;
        bestTime = 0;
        isFirstTime = true;
        times = new ArrayList<>();

        //represent the default position of the fish after it collides with border/level
        //fishPos = new int[6][2];
        fishPos = new int[][]{{10,20},{410,472},{0,500},{411,480},{405,0},{30,0}};

        fishX = 10; // 10 def
        fishY = 20; // 20 def
        fishVx = 0;
        fishVy = 0;
        levelX = 500; // 500 def
        levelY = 580; // 580 def
    }



    public void initThread()
    {
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop()
    {
        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
    }

    public void setWindow()
    {
     setBorder(BorderFactory.createLineBorder(Color.BLACK));
     
     startBtn = new JButton("Start");
     startBtn.setBounds(100,100,100,100); //does not move buttons for some reason?
     add(startBtn);
     startBtn.addActionListener(this);

     exitBtn = new JButton("Quit");
     exitBtn.setBounds(100,100,100,100);
     add(exitBtn);
     exitBtn.addActionListener(this);

     info = new JLabel();
     info.setBounds(100,100,400,100);
     add(info);
     

     try
     {
         imgLeft = ImageIO.read(new File("image/fishLeft.png"));
         imgRight = ImageIO.read(new File("image/fishRight.png"));
         imgUp = ImageIO.read(new File("image/fishUp.png"));
         imgDown = ImageIO.read(new File("image/fishDown.png"));
         hook = ImageIO.read(new File("image/hook.png"));
         hook1 = ImageIO.read(new File("image/hook1.png"));
         shark = ImageIO.read(new File("image/shark.png"));

     }
     catch (IOException e)
     {
         e.printStackTrace();
     }


    }

    public Dimension getPreferredSize()
    {
        return new Dimension(600,600);
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        setBackground(Color.BLUE);
        g.drawImage(orientedImg,fishX,fishY,this);
        g.drawImage(hook,100,0,this);
        g.drawImage(hook1,300,getHeight()-hook1.getHeight(),this);
        g.drawImage(shark,300,100,this);

        g.setColor(Color.GREEN);
        g.fillRect(levelX,levelY,100,20);

        if (!isFirstTime)
        {
            g.setColor(Color.WHITE);
            if (!isGameOver)
                elapsedTime = (new Date().getTime() - startTime) / 1000.0;
            timeString = String.format("Elapsed Time: %.2f seconds  Best Time %.2f seconds",elapsedTime,bestTime);
            g.drawString(timeString,200,50);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (gameStarted)
        {
            //Up
            if (e.getKeyCode() == 38)
            {
                fishVy = -5;
                fishVx = 0;
                orientedImg = imgUp;
            }
            //Right
            if (e.getKeyCode() == 39)
            {
                fishVx = 5;
                fishVy = 0;
                orientedImg = imgRight;
            }
            //down
            if (e.getKeyCode() == 40)
            {
                fishVy = 5;
                fishVx = 0;
                orientedImg = imgDown;
            }
            //Left
            if (e.getKeyCode() == 37)
            {
                fishVx = -5;
                fishVy = 0;
                orientedImg = imgLeft;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    // The thread, essentially the place of the animation:
    @Override
    public void run()
    {
        while (true)
        {
            //restartBorder(); (functionality like pac-man comes back from other side)
            checkCollision();
            fishX += fishVx;
            fishY += fishVy;

            repaint();

            try
            {
                Thread.sleep(1000/60);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void indCol(String name)
    {
        info.setText("You Collided with "+name);
        gameStarted = false;
        fishX = fishPos[level][0];
        fishY = fishPos[level][1];
        fishVx = 0;
        fishVy = 0;
        orientedImg = imgRight;
        exitBtn.setVisible(true);
        startBtn.setVisible(true);
    }
    public void checkCollision()
    {
        if (!gameStarted && isGameOver)
        {
            fishVx = 0;
            fishVy = 0;
            isGameOver = false;
            gameStarted = false;
            levelX = 500;
            levelY = 580;
            level = 0;
            info.setText("");
            startBtn.setText("Restart");
            JOptionPane.showMessageDialog(this,"Congrats, you won the game! Click the button to restart");
            times.add(elapsedTime);
            isFirstTime = true;
            bestTime = getMinTime();


            if (elapsedTime == bestTime && times.size() > 1)
            {
                JOptionPane.showMessageDialog(this,"Congrats! You beat your previous best");
            }

        }


        // hook's x & y
        checkEachLevelsCollision();
        if (fishX + orientedImg.getWidth() >= 125 && fishX <= 75+hook.getWidth() && fishY+orientedImg.getHeight() >= 0 && fishY <= hook.getHeight()-20)
        {
            indCol("hook");
        }
        else if (fishX+orientedImg.getWidth() >= 325 && fishX <= 280+hook1.getWidth() && fishY + orientedImg.getHeight() >= getHeight()-hook1.getHeight()+30 && fishY <= getHeight()-20)
        {
            indCol("hook1");
        }
        else if (fishX+orientedImg.getWidth() >= 350 && fishX <= 300+shark.getWidth() && fishY+orientedImg.getHeight() >= 150 && fishY <= shark.getHeight()+70)
        {
            indCol("shark");
        }
        else if (fishX < 0 || fishX > 600 || fishY < 0 || fishY > 600)
        {
            indCol("Border");
        }
//        System.out.println(fishX);
//        System.out.println(fishY);

    }

    public double getMinTime()
    {
        double min = times.get(0);

        for (int i = 1; i < times.size(); i++)
        {
            if (times.get(i) < min)
            {
                min = times.get(i);
            }
        }
        return min;
    }


    public void checkEachLevelsCollision()
    {
        if (level == 0 && fishX >= 411 && fishX <= 590 && fishY >= 480 && fishY <= 500) // manual calculation(s); do not change
        {
            setLevelUp();
            fishX = 410;
            fishY = 472;
            levelX = 0;
            levelY = 580;
        }
        else if (level == 1 && fishX >= 0 && fishX <= 60 && fishY >= 465 && fishY <= 600)
        {
            // give new level coords
            setLevelUp();
            fishX = 0;
            fishY = 500;
            levelX = 500;
            levelY = 580;

        }
        else if (level == 2 && fishX >= 411 && fishX <= 590 && fishY >= 480 && fishY <= 500)
        {
            setLevelUp();
            levelX = 500;
            levelY = 0;
        }
        else if (level == 3 && fishX >= 405 && fishY >= -5 && fishY <= 10) // make this a random collision for top right otherwise, it will be lvl3 and chaneg the coords here ->
        {
            setLevelUp();
            levelX = 0;
            levelY = 0;
        }
        else if (level == 4 && fishX >= 0 && fishX <= 30 && fishY >= -5 && fishY <= 10)
        {
            level++;
            gameStarted = false;
            fishVx = 0;
            fishVy = 0;
            startBtn.setVisible(true);
            exitBtn.setVisible(true);
        }
        else if (level == 5)
        {
            // Game is won
            isGameOver = true;
        }
    }

    public void setLevelUp()
    {
        info.setText("Congrats! You have leveled up!");
        gameStarted = false;
        fishVx = 0;
        fishVy = 0;
        orientedImg = imgUp;
        level++;
        exitBtn.setVisible(true);
        startBtn.setVisible(true);
    }

    public void restartBorder()
    {
        if (fishX >= getWidth()-100)
        {
            fishX = 1;
        }
        if (fishX <= 0)
        {
            fishX = getWidth()-101;
        }
        if (fishY <= 0)
        {
            fishY = getHeight()-101;
        }
        if (fishY >= getHeight()-100)
        {

            fishY = 1;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        exitBtn.setVisible(false);
        startBtn.setVisible(false);

        if (e.getSource().equals(exitBtn))
        {
            System.exit(0);
        }
        else if (e.getSource().equals(startBtn))
        {
            gameStarted = true;
            info.setText("");
            startBtn.setText("Resume");

            if (isFirstTime)
            {
                startTime = new Date().getTime();
                isFirstTime = false;
            }


        }
    }
}

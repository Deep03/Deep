// @author Cypher0110
/*Game working process
*We import all the necessary classes and then we create a class that extends the JFrame
*  and implements ActionListener and Keylistener
* JFrame is used to create a frame with width and height of 800
* Then we use Timer class to schedule the rendering process after certain period
* We set the frame size and call the addcolumn method
* We create a method to add columns when the game starts and continue adding after the game ends
* If gameover then the it continues to paint columns but sets bird's motion velocity to 0.
* * then we use the painColumn method to fill a red horizontal dark green column
* Then we create a jump method which is called when user pressed the space key where all the magic happen.
* ymotion is used to set the speed at which the bird will move when space is pressed

* In action performed;
* First for loop is used to loop the columns so they keep apearing
* then we have if to make the bird after the first jump instead of launching like a rocket
*  then we paint the visuals like the bird or the background
* During start , screen displays "Space to start"
* Press P to pause the game, press space to resume it.
* When you hit columns or the top of the frame screen displays GAME OVER
* Press Space to play again
* You can compete with you friends by the beating the high score set by your friend on top right corner
* Your current score is displayed on the center of the frame.
* The keyReleased is what captures if you pressed space or "P"
* @Override is used to indicate that the child class method is over-writing its base class method.
* */



//Importing classes
 import java.awt.*;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.KeyEvent;
 import java.awt.event.KeyListener;
 import java.io.*;
 import java.util.ArrayList;
 import java.util.Random;
 import javax.imageio.ImageIO;
 import javax.swing.*;

 class Main extends JFrame implements ActionListener, KeyListener
 {
//    Creating a frame
    JFrame jframe = new JFrame();
    public static Main flappyBird;

//  Screen frame size
    public final int WIDTH = 800;
    public final int HEIGHT = 800;
    public int space = 300;
    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> columns;
    public int ticks, yMotion, score, highscore;
    public boolean gameOver, started;
    public Random rand;
    public int speed = 10;
    public Image backgroundImage;
    public Image birdImage;

     public Main()
     {
         Timer timer = new Timer(1, this);
         renderer = new Renderer();
         rand = new Random();
         jframe.add(renderer);
         jframe.setTitle("Flappy bird");
         jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         jframe.setSize(WIDTH, HEIGHT);
         jframe.addKeyListener(this);
         jframe.setResizable(false);
         jframe.setVisible(true);
         bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
         columns = new ArrayList<Rectangle>();
         addColumn(true);
         addColumn(true);

         timer.start();
     }

     //     Creating one new column randomly
     public void addColumn(boolean start)
     {
//         Scale for the columns
         int width = 100;
         int height = 50 + rand.nextInt(300);

//         If the game starts the columns are added
         if (start)
         {
             columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
             columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));

         }
//       When the game ends, continue to place those columns
         else
         {
             columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
             columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
         }
     }
     //     Painting the color of the columns
     public void paintColumn(Graphics g, Rectangle column)
     {
         g.setColor(Color.green.darker());
//       Telling where to paint the color
         g.fillRect(column.x, column.y, column.width, column.height);
     }

     public void jump()
     {

//        If game's over, reset everything
         if (gameOver)
         {
             bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
             columns.clear();
             yMotion = 0;
             score = 0;
             addColumn(true);
             addColumn(true);
             gameOver = false;

         }

         if (!started)
         {
             started = true;
         }
         /*Game has started but it's not over*/
         else if (!gameOver)
         {
             if (yMotion > 0)
             {
                 yMotion = 0;
             }
             yMotion -= 7;
         }
     }

     @Override
     public void actionPerformed(ActionEvent e)
     {
         ticks++;
         if (started)
         {
             for (int i = 0; i < columns.size(); i++)
             {
                 Rectangle column = columns.get(i);
                 column.x -= speed;
             }

             if (ticks % 2 == 0 && yMotion < 15)
             {
                 yMotion += 2;
             }

             for (int i = 0; i < columns.size(); i++)
             {
                 Rectangle column = columns.get(i);

                 if (column.x + column.width < 0)
                 {
                     columns.remove(column);

                     if (column.y == 0)
                     {
                         addColumn(false);
                     }
                 }
             }
             bird.y += yMotion;
             for (Rectangle column : columns)
             {
                 /*If bird reaches center of the column, add 1 to score; checks only for the upper pipe; -10 to account for the x-axis-speed*/
                 if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10)
                 {
                     score++;
                 }
                 /*Collision Detection with the columns*/
                 if (column.intersects(bird))
                 {
                     gameOver = true;
                     /*If the bird hits the column wall, then it stays before it*/
                     if (bird.x <= column.x)
                     {
                         bird.x = column.x - bird.width;
                     }
                     else
                     {
                         /*If bird hits lower pipe top then it stays on top of it*/
                         if (column.y != 0)
                         {
                             bird.y = column.y - bird.height;
                         }
                         /*If bird hits upper pipe bottom so that it doesn't look as if it's sliding through it*/
                         else if (bird.y < column.height)
                         {
                             bird.y = column.height;
                         }
                     }
                 }
             }

             /*Collision Detection for when the bird touches the ground OR it flies away*/
             if (bird.y > HEIGHT - 120 || bird.y < 0)
             {
                 gameOver = true;
             }
             /*Condition ensures that the bird gradually falls down instead of at once*/
             if (bird.y + yMotion >= HEIGHT - 120)
             {
                 bird.y = HEIGHT - 120 - bird.height;
                 gameOver = true;
             }
         }
         renderer.repaint();
     }

     /*Painting the background, ground and the bird*/
     public void paint(Graphics g)
     {
//       setting the backgroundImage
         try {
             backgroundImage = ImageIO.read(new File("background.png"));
             g.drawImage(backgroundImage , 0 , 0 ,this);

         }  catch (IOException ex) {
             ex.printStackTrace();
         }
// setting the bird image
         try {
             birdImage = ImageIO.read(new File("bird2.png"));
             g.drawImage(birdImage , bird.x , bird.y ,this);

         }  catch (IOException ex) {
             ex.printStackTrace();
         }

//        Painting a rectangular column on the bottom
         g.setColor(Color.green);
         g.fillRect(0, HEIGHT - 120, WIDTH, 120);


         /*For each rectangle in columns ArrayList, paint it*/
         for (Rectangle column : columns)
         {
             paintColumn(g, column);
         }

         g.setColor(Color.white);
         g.setFont(new Font("Arial", 1, 100));

         /*Shows for the 1st time*/
         if (!started)
         {
             g.drawString("Space to Start!", 0, HEIGHT / 2 - 50);
         }

         /*Shows when the game is over*/
         if (gameOver)
         {
             g.drawString("Game Over!", 25, HEIGHT / 2 - 50);
//           Previous font is of 100 size but we need to reduce it to 50
             g.setFont(new Font("Arial", 1, 50));
             g.drawString("Press Space to Play Again" , 25 , HEIGHT/2 + 100);
         }

         /*Shows the score at the top when game is ongoing*/
         if (!gameOver && started)
         {
             /*Used to keep track of the highScore in the current session*/
             if(score>highscore)
                 highscore = score;

             g.drawString(String.valueOf((score)), WIDTH / 2, 100);
             g.setFont(new Font("Arial", 1, 20));
             g.drawString("High score : " + highscore, 600 , 30);

         }
     }


     public static void main(String[] args)
     {

         flappyBird = new Main();
     }

     @Override
     public void keyReleased(KeyEvent e)
     {
//         for jumping
         if (e.getKeyCode() == KeyEvent.VK_SPACE)
         {
             jump();
         }

//         for pausing
         else if (e.getKeyCode() == KeyEvent.VK_P)
         {
             started = false;
         }
     }

     @Override
     public void keyTyped(KeyEvent e)
     {
     }

     @Override
     public void keyPressed(KeyEvent e)
     {
     }
 }

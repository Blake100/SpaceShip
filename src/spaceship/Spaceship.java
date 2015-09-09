
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;
    
//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    int rocketYmovspeed;
    int rocketXmovspeed;
    boolean rocketRight;
    
    Missile missiles[] = new Missile [Missile.numMissiles];
  
    int numStars = 5;
    int starYPos[];
    int starXPos[];
    boolean ableHit[] = new boolean [numStars];
    boolean rocketHit[] = new boolean [numStars];
    int starXmovspeed;
    
    int score;
    int highScore;
    
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketYmovspeed+=2;
                   
                } else if (e.VK_DOWN == e.getKeyCode()) {
                   rocketYmovspeed-=2;
                   
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocketXmovspeed-=2;
                     rocketRight = false;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocketXmovspeed+=2;
                    rocketRight = true;
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                  

                    missiles[Missile.currentMissile].active = true;
                    missiles[Missile.currentMissile].xpos = rocketXPos;
                    missiles[Missile.currentMissile].ypos = rocketYPos;
                    missiles[Missile.currentMissile].right = rocketRight;
                    Missile.currentMissile++;
                    if (Missile.currentMissile >= Missile.numMissiles)
                        Missile.currentMissile = 0;
                       
               
                   
                                
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);

       for(int val = 0;val< numStars;val++)
        
           drawCircle(getX(starXPos[val]),getYNormal(starYPos[val]),0,2,2);
    
       for(int val = 0;val< missiles.length;val++)
          {
          
           drawBullet(getX(missiles[val].xpos),getYNormal(missiles[val].ypos-5),0,2,2);
          } 
       
        if(rocketXmovspeed <0)
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );    
        else if(rocketXmovspeed >0)
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );    
        else if(rocketRight == false && rocketXmovspeed == 0)
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );    
        else if(rocketRight && rocketXmovspeed == 0)
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );    
        
     
      g.setColor(Color.orange);
      g.drawString("Score: " + score, 20, 60);
      g.drawString("High score: " + highScore, 180, 60);
       
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.yellow);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ////////////////////////////////////////////////////////////////////////////
    public void drawBullet(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillOval(-5,-5,5,5);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

       score = 0;
        
       starYPos = new int [numStars];
       starXPos = new int [numStars];
        
//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketYmovspeed = 0;
        for(int val = 0;val< numStars;val++)
        {
        starYPos[val] = (int) (Math.random() *getHeight2());
        starXPos[val] = (int) (Math.random() *getWidth2());
        ableHit[val] = true;
        }
        for(int val = 0;val< missiles.length;val++)
        {
          missiles[val] = new Missile();
          missiles[val].missileXPosmovspeed = 0;
          missiles[val].right = false;
          missiles[val].xpos =  rocketXPos;
          missiles[val].ypos =  rocketYPos;
        }
      
         
      
        starXmovspeed = 0;
        rocketXmovspeed = 0;
        rocketRight = true;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
            bgSound = new sound("./starwars.wav"); 
        }
       if(bgSound.donePlaying)
       {
           bgSound = new sound("./starwars.wav"); 
       }
       rocketYPos = rocketYPos + rocketYmovspeed;
       
       for(int val = 0;val< numStars;val++)
        {
            starXPos[val] = starXPos[val] - rocketXmovspeed;
              if(starXPos[val] < getX() && rocketXmovspeed > 0)
              {
                 starXPos[val] = getWidth2(); 
                 starYPos[val] = (int) (Math.random() *getHeight2());
              }
              else if(starXPos[val] > getWidth2() && rocketXmovspeed < 0)
              {
                     starXPos[val] = getX();
                     starYPos[val] = (int) (Math.random() *getHeight2());
              }
        }
       if(rocketYPos >= getHeight2())
       {
           rocketYmovspeed = 0;
           rocketYPos = getHeight2();
       }
       else if(rocketYPos <= getX())
       {
           rocketYmovspeed = 0;
           rocketYPos = getX();
       }
     for (int i=0;i<missiles.length;i++)
        {
            if (missiles[i].active)
            {
                if (missiles[i].right)
                    missiles[i].xpos++;
                else
                    missiles[i].xpos--;                    
            }            
        }
              
        
       
       
      for(int val = 0;val< numStars;val++)
      {
     
                     if(rocketYPos-20 < starYPos[val] && 
                        rocketYPos+20 > starYPos[val] &&
                        rocketYPos-20 < starYPos[val] &&
                        rocketYPos+20 > starYPos[val]
                     && rocketXPos-20 < starXPos[val] && 
                        rocketXPos+20 > starXPos[val] &&
                        rocketXPos-20 < starXPos[val] &&
                        rocketXPos+20 > starXPos[val])
          {
              
              rocketHit[val]= true;
              if(rocketHit[val] && ableHit[val])        
              {
                  zsound = new sound("ouch.wav");  
                  ableHit[val] = false;
              }
               
          }
          else
          ableHit[val] = true;
      
      }
     
       for (int i=0;i<missiles.length;i++)
        {
            for (int j=0;j<numStars;j++)
            {
                if (missiles[i].active && missiles[i].xpos+10 > starXPos[j] &&
                missiles[i].xpos-10 < starXPos[j] &&
                missiles[i].ypos+10 > starYPos[j] &&
                missiles[i].ypos-10 < starYPos[j])
                {
                     missiles[i].active = false;
                     if (rocketRight)
                     {
                         starYPos[j] = (int)(Math.random()*getHeight2());
                         starXPos[j] = getWidth2();
                     }
                     else
                     {
                         starYPos[j] = (int)(Math.random()*getHeight2());
                         starXPos[j] = 0;

                     }
                }   
            }
        }  
      
      
      
      
      
      
      
      if(rocketXmovspeed >= 10)
      {
          rocketXmovspeed = 10;
      }
      else if(rocketXmovspeed <= -10)
      {
          rocketXmovspeed = -10;
      }
       
      
      
      
      
      
  }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missile
{
    
    public static int currentMissile;
    public static final int numMissiles = 100; 
   
    public int ypos;
    public int xpos;
    public int missileXPosmovspeed;
    public boolean active;
    public boolean right;
    
    Missile()
    {
       currentMissile = 0;
       active = false; 
    }
    
}
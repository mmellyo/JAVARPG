package game; 

import java.awt.Color; 
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable { 
//Runnable for multithreading.

    public static int width, height; 

    private Thread thread;  // A thread to handle the game loop (threats to multitask)

    private boolean running = false; 

    private BufferedImage img; // Image object used for rendering.

    private Graphics2D g; 


    // Constructor
    public GamePanel(int width, int height) { 
    
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height)); 
        setFocusable(true); 
        requestFocus();  
    }


    //Called when the panel is added to a container (JPanle..)
    public void addNotify() { 
    
        super.addNotify(); // Ensures the parent class behavior is executed.
        
        //if the game thread has not been created yet => create one
        if (thread == null) { 
        
            thread = new Thread(this, "GameThread"); 

            thread.start(); 
        }
    }
 
    // Initializes game resources.
    public void init() { 
   
        running = true;  // Sets the game to a running state.

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  // Creates an off-screen image for rendering.

        g = (Graphics2D) img.getGraphics(); //cast 'basic' Graphics from 'img.getGraphics()' to 'advanced' Graphics2D
    }


     // Main game loop that runs in the separate thread.
    public void run() { 
   
        init(); 

        final double GAME_HERTZ = 60.0; // the Target game updates 60 times per second.(hertz)
        final double TBU = 1000000000 / GAME_HERTZ; //UPDATE INTERVAL - Time between update (nanoseconds).
        final int MUBR = 5;   // Maximum number of updates before rendering (limits)
        final double TARGET_FPS = 60; 
        final double TTBR = 1000000000 / TARGET_FPS; // Total time before render in nanoseconds.

        double lastUpdateTime = System.nanoTime(); 
        double lastRenderTime; 

        int frameCount = 0; // Counts the number of frames rendered in a second.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000); 
        int oldFrameCount = 0; 


        while (running) { 

            double now = System.nanoTime(); 
            int updateCount = 0; 

            //Enough time has passed since the last update && The number of updates in the current cycle hasn’t exceeded the maximum limit
            while (((now - lastUpdateTime) > TBU) && (updateCount < MUBR)) {   
                
                update(); 
                input(); 

                lastUpdateTime += TBU;    

                updateCount++; 
            }

            //Prevents skipping updates when the game is lagging instead resets the timing one interval behind 'now'
            if (now - lastUpdateTime > TBU) { 
                lastUpdateTime = now - TBU; 
            }

            input(); 
            update(); 
            render(); 
            draw(); 
            // Draws the rendered image to the screen.

            lastRenderTime = now; 
            frameCount++; 

            int thisSecond = (int) (lastUpdateTime / 1000000000); 
            // Tracks the current second using 'lastUpdateTime' for timing of the updates, not 'now' the fluctuating current time 

            // Prints frame count if a new second starts.
            if (thisSecond > lastSecondTime) { 
                if (frameCount != oldFrameCount) {
                    System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                }

                frameCount = 0; //Resets the frame counter.
                lastSecondTime = thisSecond; 
                // Updates the last second time.
            }

            while ((now - lastRenderTime < TTBR) && (now - lastUpdateTime < TBU)) { 
            // Controls frame timing for smooth rendering.
                Thread.yield(); 
                // Gives other threads a chance to execute.

                try {
                    Thread.sleep(1); 
                    // Pauses the thread slightly to reduce CPU usage.
                } catch (Exception e) {
                    System.out.println("ERROR: yielding thread");
                }

                now = System.nanoTime(); 
                // Updates the current time.
            }
        }
    }

    public void update() { 
    // Updates game logic (currently empty).
    }

    public void input() { 
    // Processes user input (currently empty).
    }

    public void render() { 
    // Renders the game elements onto the BufferedImage.
        if (g != null) {
            g.setColor(new Color(66, 134, 244)); 
            // Sets the background color.

            g.fillRect(0, 0, width, height); 
            // Fills the entire panel with the background color.
        }
    }

    public void draw() { 
    // Draws the rendered image onto the panel.
        Graphics g2 = (Graphics) this.getGraphics(); 
        // Gets the panel's Graphics object.

        g2.drawImage(img, 0, 0, width, height, null); 
        // Draws the BufferedImage onto the panel.

        g2.dispose(); 
        // Releases the Graphics object.
    }
}

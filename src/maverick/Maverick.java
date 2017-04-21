package maverick;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import Handler.KeyboardHandler;
import Util.Vector;
import java.util.ArrayList;

public class Maverick extends Canvas implements Runnable {

    //Bufferstrategy and graphics to draw to the screen     
    public BufferStrategy bs;
    static public Graphics g;
    static public Graphics2D g2D;

    //Width and height of the screen with a variable to scale
    public static int WIDTH = 240;
    public static int HEIGHT = 135;
    public static int SCL = 1;

//An area in the code like the processing environment
    //Input handlers
    static KeyboardHandler keyHandler;

    //A list of all physics entities
    static ArrayList<Entity> entities;
    static ArrayList<Bullet> bullets;
    
    //Physics
    static Vector GRAVITY;
    static double appliedForce;
    static Vector FORCE;

    //The background and player objects
    static Background bg;
    static Player p;

    //Enemies
    static Plane[] enemies;
    static int enemyNum = 0;
    static double posUpdate;

    //Images
    static Image[] planeSprites;
    static int planeSpriteNum = 25;
    static Image[] bulletSprites;

    static void setup() {
        scl(4);

        keyHandler = new KeyboardHandler();

        bg = new Background(0, 0, 1);

        FORCE = new Vector(0, 0);
        
        appliedForce = SCL * SCL * 0.001 / 8.0;
        entities = new ArrayList<>();
        bullets = new ArrayList<>();
        GRAVITY = new Vector(0, appliedForce);

        p = new Player(width() / 2, height() / 2, 0, 0, 10, new Color(0, 255, 0), keyHandler);
        p.applyForce(force(p.terminalVelocity, 0.000001));

        //Creating images
        bulletSprites = new Image[3];
        planeSprites = new Image[planeSpriteNum];
        for (int i = 0; i < planeSpriteNum; i++) {
            try {
                planeSprites[i] = ImageIO.read(new File(System.getProperty("user.dir") + "/res/red_baron/red_baron" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        p.setSprite(planeSprites[12]);

        enemies = new Plane[100];
        posUpdate = 0;
        addEnemies(2);
    }

    static void draw() {
        background(51);
        bg.render();
        
        p.update();
        p.render();

        for (int i = 0; i < enemyNum; i++) {
            enemies[i].applyForce(force((Math.random() - 0.5) * appliedForce, (Math.random() - 0.5) * appliedForce));
            
            enemies[i].update();
            enemies[i].render();
        }
        posUpdate = 0;

        //Bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            bullets.get(i).render();
        }
        
        //Gravity
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).applyForce(GRAVITY);
        }
    }
//**********************************************************

//Code that runs and creates the framework that everything is built upon
    public static boolean running = false;

    Thread mainThread;

    public Maverick() {
        Dimension size = new Dimension(WIDTH * SCL, HEIGHT * SCL);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);

        addKeyListener(keyHandler);
    }

    public static void main(String[] args) {
        setup();

        //Creating the jframe
        Maverick maverick = new Maverick();

        JFrame frame = new JFrame("Maverick");
        frame.add(maverick);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //Setting the jframe to the center of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        maverick.start();

        frame.setVisible(true);
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;

        mainThread = new Thread(this, "Main");
        mainThread.start();
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;

        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (running) {
//            double timer = System.nanoTime();
            
            render();
            draw();
            renderFinish();
            
//            System.out.println(1.0 / ((System.nanoTime() - timer) / 1000000000.0));
        }
        stop();
    }

    public void render() {
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            render();
        }
        g = bs.getDrawGraphics();
        g2D = (Graphics2D) g;
    }

    public void renderFinish() {
        if (g != null) {
            g.dispose();
            g2D.dispose();
        }
        if (bs != null) {
            bs.show();
        }
    }

    public static void scl(int n) {
        if (running) {
            return;
        }

        if (n > 8) {
            n = 8;
        } else if (n < 1) {
            if (n == 0) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int newSCL = (int) Math.floor(dim.width / WIDTH);
                SCL = newSCL;
                return;
            } else if (n == -1) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int newSCL = (int) Math.floor(dim.width / WIDTH);
                SCL = newSCL - 1;
                return;

            } else {
                n = 1;
            }
        }

        SCL = n;
    }

    public static void background(int red, int green, int blue) {
        if (g != null) {
            Color prev = g.getColor();

            Color c = new Color(red, green, blue);
            g.setColor(c);
            g.fillRect(0, 0, width(), height());
            g.setColor(prev);
        }
    }

    public static void background(int grey) {
        background(grey, grey, grey);
    }

    public static int width() {
        return WIDTH * SCL + 12;
    }

    public static int height() {
        return HEIGHT * SCL + 12;
    }
    
    //Physics
    public static Vector force(double x, double y) {
        FORCE.x = x;
        FORCE.y = y;
        return FORCE;
    }

    //Enemy Functions
    public static void addEnemies(int toAdd) {
        for (int i = enemyNum; i < enemyNum + toAdd; i++) {
            enemies[i] = new Plane(width() / 2, height() / 2);
            enemies[i].setSprite(planeSprites[12]);
            enemies[i].applyForce(force(enemies[i].appliedForce, 0));
        }

        enemyNum += toAdd;
    }

    //Image manipulation methods
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}

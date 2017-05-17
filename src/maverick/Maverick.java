package maverick;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import Handler.*;
import Util.Vector;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    static MouseHandler mouseHandler;
    static int mouseX;
    static int mouseY;

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
    static ArrayList<Plane> enemies;
    static double posUpdate;
    
    //Audio
    static AudioClip bgMusic;

    //Images
    static Image[][] planeSprites;
    static int planeSpriteNum = 25;
    static Image[] bulletSprites;
    static Image menuBg;
    static Font menuFont;
    static Image levelSelect;
    static Image arrowL, arrowR;
    static String systemPath;

    //Menus
    static boolean inMenus = true;
    static int level = 0; //0 = main, 1 = level select
    static int itemNum = 5;
    static String[] items = {"Play", "Survival", "Options", "Help", "Exit"};
    static int pauseNum = 3;
    static String[] pauseItems = {"Unpause", "Change Music", "Exit"};
    static int[][] levelCoords = {{120, 315, 840, 730, 550, 520}, {250, 430, 415, 250, 235, 110}};
    static int levelNum = 6;
    static Color menuColour, hoverColour;

    //Game
    static GameManager gm;
    static int curLevel = -1;
    static int waveAlpha = 0;
    static int wave = 0;
    static boolean survival = false;
    static boolean finished = false;
    static long score = 0;
    static double finishTime = 0;
    static int lives = 0;
    static int playerType = 1;
    static boolean paused = false;

    static void setup() {
        scl(4);

        keyHandler = new KeyboardHandler();
        mouseHandler = new MouseHandler();

        FORCE = new Vector(0, 0);

        appliedForce = SCL * SCL * 0.001 / 8.0;
        entities = new ArrayList<>();
        bullets = new ArrayList<>();
        GRAVITY = new Vector(0, appliedForce);
        
        systemPath = System.getProperty("user.dir");

        try {
            menuBg = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/Menu_bg.jpg"));
            menuBg = menuBg.getScaledInstance(width(), -1, 0);

            levelSelect = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/Level_Select.jpg"));
            levelSelect = levelSelect.getScaledInstance(width(), height(), 0);

            arrowL = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/arrowL.png"));
            arrowL = arrowL.getScaledInstance(SCL * 4, SCL * 4, 0);
            arrowR = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/arrowR.png"));
            arrowR = arrowR.getScaledInstance(SCL * 4, SCL * 4, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuColour = new Color(24, 24, 24);
        hoverColour = new Color(100, 100, 100);

        try {
            menuFont = Font.createFont(Font.TRUETYPE_FONT, new File(systemPath.substring(0, systemPath.length() - 3) + "/res/Headliner.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(menuFont);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            bgMusic = Applet.newAudioClip(new URL(systemPath.substring(0, systemPath.length() - 3) + "/res/bg_music.wav"));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        //Creating images
        bulletSprites = new Image[3];
        planeSprites = new Image[2][planeSpriteNum];
        for (int i = 0; i < planeSpriteNum; i++) {
            try {
                planeSprites[0][i] = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/red_baron/red_baron" + i + ".png"));
                planeSprites[1][i] = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/red_baron/red_baron" + i + ".png"));
                planeSprites[1][i] = colorImage((BufferedImage) planeSprites[1][i], new Color(0, 0, 200));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        enemies = new ArrayList<>();
        posUpdate = 0;
    }

    static void draw() {
        if (curLevel == -1) {
            inMenus = true;
        }

        if (inMenus) {
            if (level == 0) {
                g.setFont(menuFont.deriveFont(SCL * 25f));

                g.drawImage(menuBg, 0, 0, null);
                drawOutline("Maverick", 25 * SCL, 40 * SCL, g, Color.white);
                g.setColor(menuColour);
                g.drawString("Maverick", 25 * SCL, 40 * SCL);

                g.setFont(menuFont.deriveFont(SCL * 62f / 4)); //62
                //Outline
                for (int i = 0; i < itemNum; i++) {
                    drawOutline(items[i], 25 * SCL, SCL * (230 + 60 * i) / 4, g, Color.white);
                }

                //Actual Text
                g.setColor(menuColour);
                for (int i = 0; i < itemNum; i++) {
                    if (i == 1 || i == 2) {
                        if (mouseX() > 25 * SCL && mouseX() < 60 * SCL && mouseY() > 45 * SCL + (15 * SCL * i) && mouseY() < SCL * 115 / 2 + (15 * SCL * i)) {
                            g.setColor(hoverColour);
                            if (mouseHandler.clicked()) {
                                if (i == 1) {
                                    finished = false;
                                    survival = true;
                                    level = 1;
                                }
                                if (i == 2) {

                                }
                            }
                        }
                    } else if (mouseX() > 25 * SCL && mouseX() < 45 * SCL && mouseY() > 45 * SCL + (15 * SCL * i) && mouseY() < SCL * 115 / 2 + (15 * SCL * i)) {
                        g.setColor(hoverColour);
                        if (mouseHandler.clicked()) {
                            if (i == 0) {
                                finished = false;
                                level = 1;
                            }
                            if (i == 3) {

                            }
                            if (i == 4) {
                                System.exit(0);
                            }
                        }
                    }
                    g.drawString(items[i], 25 * SCL, SCL * (230 + 60 * i) / 4);
                    g.setColor(menuColour);
                }
            } else if (level == 1) {
                g.drawImage(levelSelect, 0, 0, null);

                g.setFont(menuFont.deriveFont(SCL * 15f));
                drawOutline("Level Select", 25 * SCL, 30 * SCL, g, Color.white);
                g.setColor(menuColour);
                g.drawString("Level Select", 25 * SCL, 30 * SCL);

                g.setFont(menuFont.deriveFont(SCL * 8f));
                drawOutline("Back", SCL * 875 / 4, SCL * 70 / 4, g, Color.white);
                g.setColor(menuColour);
                if (mouseX() > 875 * SCL / 4 && mouseX() < 925 * SCL / 4 && mouseY() > 45 * SCL / 4 && mouseY() < SCL * 70 / 4) {
                    g.setColor(hoverColour);
                    if (mouseHandler.clicked()) {
                        level = 0;
                    }
                }
                g.drawString("Back", SCL * 875 / 4, SCL * 70 / 4);

                //Drawing Level Selects
                for (int i = 0; i < levelNum; i++) {
                    g.setColor(Color.white);
                    fillCircle(SCL * levelCoords[0][i] / 4, SCL * levelCoords[1][i] / 4, 4 * SCL, g);
                    g.setColor(menuColour);
                    if ((mouseX() - SCL * levelCoords[0][i] / 4) * (mouseX() - SCL * levelCoords[0][i] / 4) + (mouseY() - SCL * levelCoords[1][i] / 4) * (mouseY() - SCL * levelCoords[1][i] / 4) <= (3 * SCL) * (3 * SCL)) {
                        fillCircle(SCL * levelCoords[0][i] / 4, SCL * levelCoords[1][i] / 4, 3 * SCL + 3, g);

                        if (mouseHandler.clicked()) {
                            if (i == 0) {
                                curLevel = 0;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                            if (i == 1) {
                                curLevel = 1;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                            if (i == 2) {
                                curLevel = 2;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                            if (i == 3) {
                                curLevel = 3;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                            if (i == 4) {
                                curLevel = 4;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                            if (i == 5) {
                                curLevel = 5;
                                inMenus = false;
                                gm = new GameManager(curLevel);
                            }
                        }

                    } else {
                        fillCircle(SCL * levelCoords[0][i] / 4, SCL * levelCoords[1][i] / 4, 3 * SCL, g);
                    }
                }
            }
        } else {
            //--------------------------START OF ACTUAL GAME (NOT MENUS)----------------------------------------------------------------------------------------------------
            if (!paused) {
                gm.update();

                background(51);
                bg.render();

                //Bullet rendering
                for (int i = 0; i < bullets.size(); i++) {
                    bullets.get(i).render();
                }

                //Player update and render
                p.update();
                p.render();

                if (p.destroyed) {
                    lives--;
                    p.destroyed = false;
                    p.health = 100 - 50 * playerType;

                    p.pos = new Vector(width() / 2, height() / 2);
                    p.vel = new Vector(0, 0);

                    double delta = -bg.pos.x;
                    bg.pos = new Vector(0, 0);
                    for (int i = 0; i < enemies.size(); i++) {
                        Plane e = enemies.get(i);
                        e.pos.x += delta;
                    }

                    p.applyForce(force(p.terminalVelocity, 0.0000001));
                }

                if (lives <= 0 && !finished) {
                    finished = true;
                    finishTime = System.nanoTime();
                }

                //Enemy update and render
                for (int i = enemies.size() - 1; i >= 0; i--) {
                    Plane enemy = enemies.get(i);
                    Vector tempVel = p.pos.sub(enemy.pos);
                    if (tempVel.magSq() <= (SCL * SCL * 100 * 100)) {
                        tempVel.setMag(appliedForce);
                    } else {
                        tempVel = enemy.vel.copy();
                        tempVel.setMag(appliedForce / 2);
                    }
                    if (enemy.vel.y > 0) {
                        tempVel.y -= appliedForce / 2;
                    }
                    enemy.applyForce(tempVel);

                    enemies.get(i).update();
                    enemies.get(i).render();

                    if (enemies.get(i).pos.x < -10 * SCL) {
                        g.drawImage(arrowL, 0, enemies.get(i).pos.getY(), null);
                    } else if (enemies.get(i).pos.x > width() + 10 * SCL) {
                        g.drawImage(arrowR, width() - arrowR.getWidth(null), enemies.get(i).pos.getY(), null);
                    }

                    if (enemies.get(i).destroyed) {
                        enemies.remove(i);
                        score += 100;
                    }
                }

                //Bullet update
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    bullets.get(i).update();
                    bullets.get(i).pos.add(force(-posUpdate, 0));

                    if (bullets.get(i).destroyed) {
                        bullets.remove(i);
                    }
                }
                posUpdate = 0;

                //Wave
                if (waveAlpha > 0) {
                    g.setColor(new Color(255, 255, 255, waveAlpha));
                    g.setFont(menuFont.deriveFont(SCL * 50f / 4));
                    g.drawString("Wave " + wave, width() / 2 - (SCL * 50 / 4), height() / 2);
                }

                //HUD
                g.setFont(menuFont.deriveFont(SCL * 35f / 4));
                g.setColor(Color.white);
                g.drawString("Health: " + (int) (100 * p.health / (100 - 50 * playerType)), 5 * SCL, height() - 5 * SCL);
                g.drawString("Lives: " + lives, width() - SCL * 22, SCL * 10);
                g.drawString("Score: " + score, 5 * SCL, SCL * 10);

                if (mouseX() >= width() - SCL * 18 && mouseX() <= width() && mouseY() >= height() - SCL * 10 && mouseY() <= height()) {
                    g.setColor(hoverColour);
                    if (mouseHandler.clicked()) {
                        paused = true;
                    }
                }
                g.drawString("Pause", width() - SCL * 18, height() - 5 * SCL);

                //Gravity
                for (int i = 0; i < entities.size(); i++) {
//                entities.get(i).updateConstants(2); //Higher means slower
                    entities.get(i).applyForce(force(0, entities.get(i).appliedForce));
                }
            } else {
                g.setFont(menuFont.deriveFont(SCL * 75f / 4));
                drawOutline("Paused", SCL * 20, SCL * 40, g, Color.white);
                g.setColor(menuColour);
                g.drawString("Paused", SCL * 20, SCL * 40);

                g.setFont(menuFont.deriveFont(SCL * 35f / 4));
                for (int i = 0; i < pauseNum; i++) { //440, 250
                    drawOutline(pauseItems[i], SCL * 20, SCL * 52 + (SCL * 10 * i), g, Color.white);
                    g.setColor(menuColour);
                    if (mouseX() > SCL * 20 && mouseX() <= pauseItems.length * SCL * (80 / 7) && mouseY() >= SCL * 52 + (SCL * 10 * i) - SCL * 35 / 4 && mouseY() <= SCL * 52 + (SCL * 10 * i)) {
                        g.setColor(hoverColour);
                        if (mouseHandler.clicked()) {
                            if (i == 0) {
                                paused = false;
                                break;
                            } else if (i == 1) {

                            } else if (i == 2) {
                                reset();
                                break;
                            }
                        }
                    }
                    g.drawString(pauseItems[i], SCL * 20, SCL * 52 + (SCL * 10 * i));
                }

            }
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
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
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

            //Checking if finished
            if (finished) {
                g.setFont(menuFont.deriveFont(SCL * 50f / 4));
                g.setColor(Color.white);
                if (lives > 0) {
                    g.drawString("Level Complete", width() / 2 - (24 * SCL), height() / 2 - (SCL * 25 / 4));
                } else {
                    g.drawString("Level Failed", width() / 2 - (20 * SCL), height() / 2 - (SCL * 25 / 4));
                }

                g.setFont(menuFont.deriveFont(SCL * 40f / 4));
                g.drawString("Score: " + score, width() / 2 - (SCL * 10), height() / 2 + (SCL * 25 / 4));

                if (System.nanoTime() - finishTime >= 5000000000.0) {
                    reset();
                }
                if (System.nanoTime() - finishTime >= 4000000000.0) {
//                    g.setColor(new Color(0, 0, 0, (int) (255 * (System.nanoTime() - finishTime - 5000000000.0) / 1000000000.0) % 255));
                    g.setColor(Color.BLACK);
                    g.drawRect(0, 0, width(), height());
                }
            }

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
        for (int i = 0; i < toAdd; i++) {
            int last = enemies.size();
            if (Math.random() <= 0.5) {
                enemies.add(new Plane(bg.pos.getX(), (int) ((Math.random() * (bg.height - SCL * 6)) + SCL * 3)));
                enemies.get(last).setSprite(planeSprites[enemies.get(last).type][12]);
                enemies.get(last).applyForce(force(enemies.get(last).appliedForce, 0));
            } else {
                enemies.add(new Plane(bg.pos.getX() + bg.width, (int) ((Math.random() * (bg.height - SCL * 6)) + SCL * 3)));
                enemies.get(last).setSprite(planeSprites[enemies.get(last).type][12]);
                enemies.get(last).applyForce(force(-enemies.get(last).appliedForce, 0));
            }
        }
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

    public static BufferedImage colorImage(BufferedImage image, Color c) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);

                pixels[0] += c.getRed();
                if (pixels[0] > 255) {
                    pixels[0] = 255;
                }

                pixels[1] += c.getGreen();
                if (pixels[1] > 255) {
                    pixels[1] = 255;
                }

                pixels[2] += c.getBlue();
                if (pixels[2] > 255) {
                    pixels[2] = 255;
                }

                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }

    //Text functions
    public static void drawOutline(String s, int x, int y, Graphics g, Color c) {
        g.setColor(c);
        g.drawString(s, x - 1, y - 1);
        g.drawString(s, x - 1, y + 1);
        g.drawString(s, x + 1, y - 1);
        g.drawString(s, x + 1, y + 1);
    }

    //Mouse Functions
    public static int mouseX() {
        return mouseHandler.mouseX();
    }

    public static int mouseY() {
        return mouseHandler.mouseY();
    }

    //Drawing functions
    public static void drawCircle(int x, int y, int r, Graphics g) {
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
    }

    public static void fillCircle(int x, int y, int r, Graphics g) {
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    //RESET
    public static void reset() {
        finishTime = 0;
        finished = false;
        level = 0;
        inMenus = true;
        gm = null;
        survival = false;
        lives = 0;
        paused = false;

        for (int i = enemies.size() - 1; i >= 0; i--) {
            enemies.remove(i);
        }
    }

}

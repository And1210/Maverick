package maverick;

import Util.Vector;
import java.awt.*;
import static maverick.Maverick.*;

public class Entity {

    public Vector pos;
    public Vector vel;
    public Vector acc;
    int r;
    Color c;
    double terminalVelocity;
    double airResistanceRatio = 0.001; //terminalVelocity = 1 / (1000 * airResistanceRatio)
    double appliedForce = 0;

    Entity(int x, int y, int velX, int velY, int r_, Color c_) {
        pos = new Vector(x, y);
        vel = new Vector(velX, velY);
        acc = new Vector(0, 0);

        r = (int) (SCL * r_ / 4.0);
        c = c_;

        updateConstants(1);
    }

    Entity(int x, int y, int r, Color c) {
        this(x, y, 0, 0, r, c);
    }

    Entity(int r) {
        this(width() / 2, height() / 2, r, new Color(0, 0, 0));
    }

    Entity() {
        this(width() / 100);
    }

    public void update() {
        pos.add(vel);
        vel.add(acc);
        acc.mult(0);

        //Air resistance
        applyForce(vel.multCopy(-airResistanceRatio));
    }

    public void render() {
        g.setColor(c);
        g.fillOval(pos.getX() - r, pos.getY() - r, 2 * r, 2 * r);
    }

    //Physics functions
    public void applyForce(Vector force) {
        acc.add(force);
    }

    public void constrain(int x1, int y1, int x2, int y2) {
        if (pos.getX() - r < x1) {
            pos.setX(x1 + r + 1);
            vel.x *= -1;
        }
        if (pos.getX() + r > x2) {
            pos.setX(x2 - r - 1);
            vel.x *= -1;
        }
        if (pos.getY() - r < y1) {
            pos.setY(y1 + r + 1);
            vel.y *= -1;
        }
        if (pos.getY() + r > y2) {
            pos.setY(y2 - r - 1);
            vel.y *= -1;
        }
    }
    
    public void updateConstants(double n) {
        appliedForce = SCL * SCL * 0.001 / (4.0 * n);
        terminalVelocity = SCL * SCL / (16.0 * n);
        airResistanceRatio = 1 / (1000 * terminalVelocity);
    }

    //Getters and Setters
    public void setTerminalVelocity(double n) {
        terminalVelocity = SCL * n / 8.0;

        airResistanceRatio = 1 / (1000 * terminalVelocity);
    }
    
    public int getX() {
        return pos.getX();
    }
    
    public int getY() {
        return pos.getY();
    }
}

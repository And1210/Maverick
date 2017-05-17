package maverick;

import Handler.KeyboardHandler;
import Util.Vector;
import java.awt.*;
import static maverick.Maverick.*;

public class Player extends Plane {

    KeyboardHandler keyHandler;

    public Player(int x, int y, int velX, int velY, int r, Color c, KeyboardHandler keyHandler_, int planeType) {
        super(x, y, velX, velY, r, c);

        type = planeType;
        updateConstants(2 - type * 0.5);
        if (type == 0) {
            health = 100;
        } else {
            health = 50;
        }
        
        keyHandler = keyHandler_;
    }

    public void update() {
        //Movement checks
        movementChecks();

        //Updating position and velocity
        pos.y += vel.y;
        vel.add(acc);
        acc.mult(0);

        applyInput();

        updateAngle();

        flipCheck();

        //Air resistance
        applyForce(vel.multCopy(-airResistanceRatio));

        //Thrust
        if (!thrusting) {
            if (vel.magSq() < terminalVelocity * terminalVelocity) {
                vel.setMag(terminalVelocity);
            }
        } else {
            if (vel.magSq() < terminalVelocity * terminalVelocity / 4.0) {
                vel.setMag(terminalVelocity / 2.0);
            }
        }

        //Lift
        applyForce(force(0, -appliedForce * Math.abs(Math.cos(angle))));

        //Hitbox updates
        int hwidth = sprite.getWidth(null) / 2, hheight = sprite.getHeight(null) / 2;
        xpoints[0] = (int) (pos.x - hwidth * Math.cos(curAngle + Math.PI / 9));
        xpoints[1] = (int) (pos.x + hwidth * Math.cos(curAngle - Math.PI / 9));
        xpoints[2] = (int) (pos.x + hwidth * Math.cos(curAngle + Math.PI / 9));
        xpoints[3] = (int) (pos.x - hwidth * Math.cos(curAngle - Math.PI / 9));
        ypoints[0] = (int) (pos.y - hwidth * Math.sin(curAngle + Math.PI / 9));
        ypoints[1] = (int) (pos.y + hwidth * Math.sin(curAngle - Math.PI / 9));
        ypoints[2] = (int) (pos.y + hwidth * Math.sin(curAngle + Math.PI / 9));
        ypoints[3] = (int) (pos.y - hwidth * Math.sin(curAngle - Math.PI / 9));
        for (int i = 0; i < 4; i++) {
            hitbox.addPoint(xpoints[i], ypoints[i]);
        }
//        g.setColor(Color.BLACK);
//        g.drawPolygon(hitbox);

        //Checking if hit
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            if (b.tag == true) {
                if (hitbox.contains(b.pos.x, b.pos.y)) {
                    b.destroy();
                    health -= 5;
                }
            }
        }
        hitbox.reset();

        constrain(0, 0, width(), height());

        //End of update to-dos
        updateCount++;
        thrusting = false;
        
        if (health <= 0) {
            destroy();
        }
    }

    //Input funtions
    public void applyInput() {
        if (keyHandler.isPressed('W')) {
            applyForce(force(0, -appliedForce));
            thrusting = true;
        }
        if (keyHandler.isPressed('S')) {
            applyForce(force(0, appliedForce));
            thrusting = true;
        }
        if (keyHandler.isPressed('A')) {
            applyForce(force(-appliedForce, 0));
            thrusting = true;
        }
        if (keyHandler.isPressed('D')) {
            applyForce(force(appliedForce, 0));
            thrusting = true;
        }

        if (keyHandler.isPressed(' ') || mouseHandler.clicked()) {
            if (System.nanoTime() - shotCooldown > fireRate * 1000000) {
                shoot(false);
                shotCooldown = System.nanoTime();
            }
        }
    }

    //Getters and Setters
    public void setKeyHandler(KeyboardHandler keyHandler_) {
        keyHandler = keyHandler_;
    }

    //Physics
    public void movementChecks() {
        if (bg.getX() > 0) {
            if (getX() > width() / 2 && vel.x > 0) {
                bg.pos.x += -vel.x;
                pos.x = width() / 2;
                posUpdate += vel.x;
            } else {
                pos.x += vel.x;
            }
        } else if (bg.getX() < -bg.width + width()) {
            if (getX() < width() / 2 && vel.x < 0) {
                bg.pos.x += -vel.x;
                pos.x = width() / 2;
                posUpdate += vel.x;
            } else {
                pos.x += vel.x;
            }
        } else {
            bg.pos.x += -vel.x;
            posUpdate += vel.x;
        }
    }
    
    //Health
    public void destroy() {
        health = 0;
        destroyed = true;
    }
}

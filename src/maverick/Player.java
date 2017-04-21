package maverick;

import Handler.KeyboardHandler;
import Util.Vector;
import java.awt.*;
import static maverick.Maverick.*;

public class Player extends Plane {

    KeyboardHandler keyHandler;

    public Player(int x, int y, int velX, int velY, int r, Color c, KeyboardHandler keyHandler_) {
        super(x, y, velX, velY, r, c);

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

        constrain(0, 0, width(), height());

        //End of update to-dos
        updateCount++;
        thrusting = false;
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
        
        if (keyHandler.isPressed(' ')) {
            shoot();
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
}

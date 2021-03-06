package maverick;

import Util.Vector;
import java.awt.*;
import java.awt.geom.AffineTransform;
import static maverick.Maverick.*;

public class Plane extends Entity {

    Image sprite;

    double curAngle = Math.PI;
    double angle = 0;

    boolean facing = true; //true = right, false = left
    boolean flipping = false;

    int updateCount = 0;
    int curSprite = 0;

    double shotCooldown = 0;
    int fireRate = 100;
    int health = 100;
    int type = 0; //0 = health, 1 = speed
    boolean destroyed = false;
    Polygon hitbox;
    int[] xpoints, ypoints;

    boolean onScreen = false;

    Vector target;
    boolean turning = false;
    double turnTimer = 0;

    boolean thrusting = false;

    public Plane(int x, int y, int velX, int velY, int r, Color c) {
        super(x, y, velX, velY, r, c);

        hitbox = new Polygon();
        xpoints = new int[4];
        ypoints = new int[4];

        setSprite(planeSprites[type][12]);

        setRandomType();
        if (type == 0) {
            health = 100;
        } else {
            health = 50;
        }

        target = new Vector(appliedForce, appliedForce);

        entities.add(this);
    }

    public Plane(int x, int y) {
        this(x, y, 0, 0, 10, new Color(0, 0, 0));
    }

    public void update() {
        //Updating position and velocity
        pos.add(force(-posUpdate, 0));
        pos.add(vel);
        vel.add(acc);

        //Checking if any thrust is being applied
        if (acc.magSq() == 0) {
            thrusting = false;
        } else {
            thrusting = true;
        }
        acc.mult(0);

        updateAngle();

        flipCheck();

//        //Air resistance
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

        constrain(bg.getX(), bg.getY(), bg.getX() + bg.width, bg.getY() + bg.height);

        //Shooting AI
        if (Math.random() < 0.0005) {
            shoot(true);
        }

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
            if (b.tag == false) {
                if (hitbox.contains(b.pos.x, b.pos.y)) {
                    b.destroy();
                    health -= 5;
                    score += 5;
                }
            }
        }
        hitbox.reset();

        //Health check
        if (health <= 0) {
            destroy();
        }

        //End of update to-dos
        updateCount++;
    }

    public void render() {
        if (sprite == null) {
            super.render();
        } else {

            //Drawing the image with rotation
            if (inside(-SCL * 10, -SCL * 10, width() + SCL * 10, height() + SCL * 10)) {
                onScreen = true;
//                AffineTransform tx = AffineTransform.getRotateInstance(curAngle, spriteBuf.getWidth(null) / 2, spriteBuf.getHeight(null) / 2);
//                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
//                g2D.drawImage(op.filter(spriteBuf, null), getX() - spriteBuf.getWidth(null) / 2, getY() - spriteBuf.getHeight(null) / 2, null);

                AffineTransform old = g2D.getTransform();
                g2D.translate(pos.x - sprite.getWidth(null) / 2, pos.y - sprite.getHeight(null) / 2);
                g2D.rotate(curAngle, sprite.getWidth(null) / 2, sprite.getHeight(null) / 2);
                g2D.drawImage(sprite, 0, 0, null);
                g2D.setTransform(old);
            } else {
                onScreen = false;
            }
        }
    }

    //Variable checks and updates
    public void updateAngle() {
        if (vel.x != 0) {
            double r = vel.y / vel.x;

            if (vel.x < 0 && vel.y < 0) {
                curAngle = Math.atan(r);
            } else if (vel.x < 0 && vel.y > 0) {
                curAngle = Math.atan(r);
            } else {
                curAngle = Math.PI + Math.atan(r);
            }
        }

        angle = Math.PI - curAngle;
    }

    public void flipCheck() {
        if (!flipping) {
            if (facing) {
                if (curAngle >= 11 * Math.PI / 6 || curAngle <= 1 * Math.PI / 6) {
                    facing = !facing;
                    flipping = true;
                }
            } else {
                if (curAngle >= 5 * Math.PI / 6 && curAngle <= 7 * Math.PI / 6) {
                    facing = !facing;
                    flipping = true;
                }
            }
        } else {
            if (updateCount % 25 == 0) {
                curSprite++;
                if (facing) {
                    if (curSprite >= 12) {
                        curSprite = 12;
                        flipping = false;
                    }
                } else {
                    if (curSprite >= 25) {
                        curSprite = 0;
                        flipping = false;
                    }
                }
                setSprite(planeSprites[type][curSprite]);
            }
        }
    }

    //Getters and Setters
    public void setSprite(Image img) {
        sprite = img.getScaledInstance(-1, SCL * 75 / 4, Image.SCALE_REPLICATE);
    }

    public void setRandomType() {
        type = (int) (Math.random() * 2);
        updateConstants(2 - type * 0.5);
    }

    //Physics
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

    public boolean inside(int x1, int y1, int x2, int y2) {
        boolean out = false;
        if (pos.getX() >= x1 && pos.getX() <= x2 && pos.getY() >= y1 && pos.getY() <= y2) {
            out = true;
        }

        return out;
    }

    //Attacking
    public void shoot(boolean t) {
        bullets.add(new Bullet(pos.copy(), vel.multCopy(3), 0, t));
    }

    //Health
    public void destroy() {
        destroyed = true;
    }

    //AI
    private Vector AI() {
        double limit = appliedForce;

        if (System.nanoTime() - turnTimer > 2000000000.0) {
            turning = false;
        }
        
        if (!turning) {
            if (p.pos.sub(pos).magSq() <= (SCL * SCL * 100 * 100)) {
                target.x = p.pos.x;
                target.y = p.pos.y;
            }
            if (vel.y > 0) {
                System.out.println("WARNING");
                target.x = pos.x + vel.x;
                target.y = pos.y - 1;
            } else {
                System.out.println("GOOD");
                target.x = pos.x + vel.x;
                target.y = pos.y;
            }
            if (Math.random() <= 0.01) {
                target.y = pos.y - SCL * 20;
                turning = true;
                turnTimer = System.nanoTime();
            }

            if (target.magSq() < limit * limit) {
                target.x = pos.x + Math.random() - 0.5;
                target.y = pos.y + Math.random() - 0.5;
            }
        }
        target.limit(limit);
        target.y += -0.000000001;

        Vector steering;
        steering = target.sub(vel);
//        System.out.print(steering + "   ");
        steering.add(acc);
//        System.out.println(steering);

        steering.limit(limit);

        return steering;
    }
}

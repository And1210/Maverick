package maverick;

import Util.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import static maverick.Maverick.*;

public class Bullet {

    Vector pos;
    Vector vel;

    Image sprite;

    int type = 0; //3 total: 0 = cannon, 1 = RPG, 2 = laser

    double angle = 0;

    public Bullet(Vector position, Vector velocity, int type_) {
        pos = position;
        vel = velocity;

        type = type_;
        sprite = bulletSprites[type];
        updateAngle();
    }

    public void update() {
        pos.add(vel);

        updateAngle();
    }

    public void render() {
        if (sprite != null) {
            AffineTransform old = g2D.getTransform();
            g2D.translate(pos.x - sprite.getWidth(null) / 2, pos.y - sprite.getHeight(null) / 2);
            g2D.rotate(angle, sprite.getWidth(null) / 2, sprite.getHeight(null) / 2);
            g2D.drawImage(sprite, 0, 0, null);
            g2D.setTransform(old);
        } 
    }

    public void updateAngle() {
        if (vel.x != 0) {
            double r = vel.y / vel.x;

            if (vel.x < 0 && vel.y < 0) {
                angle = Math.atan(r);
            } else if (vel.x < 0 && vel.y > 0) {
                angle = Math.atan(r);
            } else {
                angle = Math.PI + Math.atan(r);
            }
        }
    }

}

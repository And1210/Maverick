package maverick;

import Util.*;
import java.awt.*;

public class Bullet {
    Vector pos;
    Vector vel;
    
    Image sprite;
    
    public Bullet(double x, double y, double velX, double velY) {
        pos = new Vector(x, y);
        vel = new Vector(velX, velY);
    }
    
    public void update() {
        pos.add(vel);
    }
    
}

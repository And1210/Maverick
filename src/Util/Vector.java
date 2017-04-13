package Util;

public final class Vector {
    
    public double x, y;
    
    public Vector(double xIn, double yIn) {
        x = xIn;
        y = yIn;
    }
    
    public void add(Vector v) {
        x += v.x;
        y += v.y;
    }
    
    public Vector sub(Vector v) {
        Vector out = new Vector(x - v.x, y - v.y);
        return out;
    }
    
    public void mult(double n) {
        x *= n;
        y *= n;
    }
    
    public Vector multCopy(double n) {
        Vector out = new Vector(x * n, y * n);
        return out;
    }
    
    public void div(double n) {
        mult(1 / n);
    }
    
    public void setMag(double n) {
        if (x == 0 && y == 0) {
            x = 1;
            y = 1;
        } else if (x == 0 || y == 0)
            return;
        double r = x / y;
        if (y < 0)
            y = -Math.pow((n * n) / (r * r + 1), 0.5);
        else
            y = Math.pow((n * n) / (r * r + 1), 0.5);
        if (x < 0)
            x = -Math.abs(r * y);
        else
            x = Math.abs(r * y);
    }
    
    public double mag() {
        return Math.pow(x * x + y * y, 0.5);
    }
    
    public double magSq() {
        return x * x + y * y;
    }
    
    public void limit(double n) {
        if (mag() > n)
            setMag(n);
    }
    
    public Vector copy() {
        Vector out = new Vector(x, y);
        return out;
    }
    
    public int getX() {
        return (int) x;
    }
    
    public int getY() {
        return (int) y;
    }
    
    public void setX(int xIn) {
        x = xIn;
    }
    
    public void setY(int yIn) {
        y = yIn;
    }
    
    public String toString() {
        return x + " " + y;
    }
}

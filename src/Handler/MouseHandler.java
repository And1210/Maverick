package Handler;

import java.awt.event.*;

public class MouseHandler extends MouseAdapter {

    private int mouseX = 0, mouseY = 0;
    private boolean clicked;
    
    public void mouseClicked(MouseEvent e) {
        
    }
    
    public void mousePressed(MouseEvent e) {
        clicked = true;
        System.out.println(e.getX() + " " + e.getY());
    }
    
    public void mouseReleased(MouseEvent e) {
        clicked = false;
    }
    
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    public void mouseEntered(MouseEvent e) {
        
    }
    
    public void mouseExited(MouseEvent e) {
        
    }
    
    public void mouseDragged(MouseEvent e) {
        
    }
    
    public int mouseX() {
        return mouseX;
    }
    
    public int mouseY() {
        return mouseY;
    }
    
    public boolean clicked() {
        return clicked;
    }
    
}

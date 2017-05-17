package Handler;

import Util.*;
import java.awt.event.*;

public class KeyboardHandler implements KeyListener {

    Queue stream = new Queue();
    static boolean[] keyPressed = new boolean[256];

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        //stream.enqueue(key);
        
        keyPressed[key] = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        keyPressed[key] = false;
    }
    
    public boolean isPressed(char key) {
        return keyPressed[(int)key];
    }

    public char getChar() {
        return (char) stream.dequeue();
    }

    public boolean charAvailable() {
        boolean out = false;
        if (stream.size() > 0) {
            out = true;
        }

        return out;
    }

}

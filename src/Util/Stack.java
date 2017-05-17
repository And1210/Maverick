package Util;

public class Stack {

    Node top = null;

    public Stack() {
        top = null;
    }

    public void push(int in) {
        if (top == null) {
            top = new Node(in, null);
        } else {
            top = new Node(in, top);
        }
    }

    public int pop() {
        int out = 0;
        try {
            out = top.data;
            top = top.next;
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
        return out;
    }

    private class Node {

        int data;
        Node next;

        Node(int data_, Node next_) {
            data = data_;
            next = next_;
        }
    }
}

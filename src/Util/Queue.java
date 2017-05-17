package Util;

public class Queue {

    public Node top = null;

    public Queue() {
        top = null;
    }

    public void enqueue(int in) {
        if (top == null) {
            top = new Node(in, null);
        } else {
            top = new Node(in, top);
        }
    }

    public int dequeue() {
        int out = 0;
        if (top != null) {
            if (top.next == null) {
                out = top.data;
                top = null;
            } else {
                Node cur = top;
                Node prev = cur;
                while (cur.next != null) {
                    prev = cur;
                    cur = cur.next;
                }
                out = cur.data;
                prev.next = null;
            }
        }

        return out;
    }

    public int size() {
        int out = 0;
        Node cur = top;
        while (cur != null) {
            out++;
            cur = cur.next;
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

public class Rectangle {
    public int x1;
    public int x2;
    public int y1;
    public int y2;

    public Rectangle(Point p1, Point p2) {
        x1 = Math.min(p1.x, p2.x);
        x2 = Math.max(p1.x, p2.x);
        y1 = Math.min(p1.y, p2.y);
        y2 = Math.max(p1.y, p2.y);
    }    

    public Rectangle(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
    public Rectangle(Rectangle rectangle) {
        this.x1 = rectangle.x1;
        this.x2 = rectangle.x2;
        this.y1 = rectangle.y1;
        this.y2 = rectangle.y2;
    }
    
    public boolean contains(Rectangle rectangle) {
        if (x1 <= rectangle.x1 && x2 >= rectangle.x2 && y1 <= rectangle.y1 && y2 >= rectangle.y2) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Rectangle("+ x1 + ", " + y1 + "), (" + x2 + ", " + y2 + "))";
    }
}

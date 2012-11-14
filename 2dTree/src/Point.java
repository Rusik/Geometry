
import java.util.Comparator;


public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;        
    }
    
    public static class XPointComparator implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            return p1.x - p2.x;
        }
    }

    public static class YPointComparator implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            return p1.y - p2.y;
        }
    }
    
    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ')';
    }
    
    public XPointComparator getXComparator() {
        return new XPointComparator();
    }
    
    public YPointComparator getYComparator() {
        return new YPointComparator();
    }

}

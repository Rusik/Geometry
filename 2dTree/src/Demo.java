
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import javax.swing.*;

public class Demo extends JApplet {

    static final int pointSize = 5;
    private List<Point> points;
    private List<Point> highlightedPoints;
    private Point startRectPoint;
    private Point endRectPoint;

    public Demo() {
        points = new ArrayList<Point>();
        highlightedPoints = new ArrayList<Point>();
        startRectPoint = new Point(0, 0);
        endRectPoint = new Point(0, 0);
    }

    public void addPoint(Point point) {
        points.add(point);
        repaint();
    }

    public void setStartRectanglePoint(Point point) {
        startRectPoint = point;
        endRectPoint = point;
        highlightedPoints.removeAll(highlightedPoints);
    }

    public void setEndRectanglePoint(Point point) {
        endRectPoint = point;
        repaint();
    }

    public Rectangle getRectangle() {
        return new Rectangle(startRectPoint, endRectPoint);
    }

    public void search() {
        Tree2d tree = new Tree2d(points);
//        System.out.println(tree);
        setHighlightedPoints(tree.findPoints(getRectangle()));
        repaint();
    }

    public void clear() {
        points.removeAll(points);
        highlightedPoints.removeAll(highlightedPoints);
        startRectPoint = new Point(-2, -2);
        endRectPoint = new Point(-2, -2);
        repaint();
    }

    public void setHighlightedPoints(List<Point> points) {
        highlightedPoints = points;
    }

    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = getSize();

        //clear
        g2.setColor(Color.white);
        g2.fill(new Rectangle2D.Double(0, 0, d.width, d.height));

        //draw points
        g2.setPaint(Color.black);
        for (Point point : points) {
            g2.fill(new Rectangle2D.Double(point.x * pointSize, point.y * pointSize, pointSize, pointSize));
        }

        //draw rectangle
        if (startRectPoint != null) {
            Rectangle rectangle = getRectangle();
            int width = (rectangle.x2 - rectangle.x1 + 1) * pointSize;
            int height = (rectangle.y2 - rectangle.y1 + 1) * pointSize;
            g2.draw(new Rectangle2D.Double(rectangle.x1 * pointSize, rectangle.y1 * pointSize, width, height));
        }

        //draw highlighted points
        g2.setColor(Color.red);
        for (Point point : highlightedPoints) {
            g2.fill(new Rectangle2D.Double(point.x * pointSize, point.y * pointSize, pointSize, pointSize));
        }

    }
    static Demo applet;

    public static void main(String[] args) {

        if (args.length > 0) {
            String param = args[0];
            if (param.equalsIgnoreCase("-gui")) {

                JFrame f = new JFrame("2d-Tree Demo");

                f.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });

                applet = new Demo();
                f.getContentPane().add("Center", applet);
                applet.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        int x;
                        int y;
                        x = me.getX() / pointSize;
                        y = me.getY() / pointSize;
                        applet.addPoint(new Point(x, y));
                    }

                    public void mousePressed(MouseEvent me) {
                        int x;
                        int y;
                        x = me.getX() / pointSize;
                        y = me.getY() / pointSize;
                        applet.setStartRectanglePoint(new Point(x, y));
                    }

                    public void mouseReleased(MouseEvent me) {
                        int x;
                        int y;
                        x = me.getX() / pointSize;
                        y = me.getY() / pointSize;
                        applet.setEndRectanglePoint(new Point(x, y));
                        applet.search();
                    }
                });

                applet.addMouseMotionListener(new MouseMotionListener() {
                    @Override
                    public void mouseDragged(MouseEvent me) {
                        int x;
                        int y;
                        x = me.getX() / pointSize;
                        y = me.getY() / pointSize;
                        applet.setEndRectanglePoint(new Point(x, y));
                    }

                    @Override
                    public void mouseMoved(MouseEvent me) {
                    }
                });

                applet.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent ke) {
                        applet.clear();
                    }
                });

                applet.init();
                applet.clear();

                f.pack();
                f.setSize(new Dimension(600, 600));
                f.setLocation(400, 200);
                f.setResizable(false);
                f.setVisible(true);
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            try {
                
                List<Point> points = new ArrayList<Point>();
                List<Rectangle> rectangles = new ArrayList<Rectangle>();
                
                String line = in.readLine();
                int pointsNumber = Integer.parseInt(line);
                
                for (int i = 0; i < pointsNumber; i++) {
                    line = in.readLine();
                    
                    Scanner s = new Scanner(line);
                    s.findInLine("\\((\\d+),(\\s*)(\\d+)\\)");
                    MatchResult result = s.match();
                    int x = Integer.parseInt(result.group(1));
                    int y = Integer.parseInt(result.group(3));
                    Point p = new Point(x, y);
                    points.add(p);
                }
                
                line = in.readLine();
                int rectNumber = Integer.parseInt(line);
                
                for (int i = 0; i < rectNumber; i++) {
                    line = in.readLine();
                    
                    Scanner s = new Scanner(line);
                    s.findInLine("\\((\\d+),(\\s*)(\\d+),(\\s*)(\\d+),(\\s*)(\\d+)\\)");
                    MatchResult result = s.match();
                    int x1 = Integer.parseInt(result.group(1));
                    int y1 = Integer.parseInt(result.group(3));
                    int x2 = Integer.parseInt(result.group(5));
                    int y2 = Integer.parseInt(result.group(7));
                    Rectangle rectangle = new Rectangle(x1, x2, y1, y2);
                    rectangles.add(rectangle);
                }

                Tree2d tree = new Tree2d(points);
                for (Rectangle rectangle : rectangles) {
                    System.out.println(tree.findPoints(rectangle).size());
                }
                
            } catch (IOException ex) {
                Logger.getLogger(Demo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

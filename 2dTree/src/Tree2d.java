
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;


public class Tree2d {
    
    private enum NodeType {
        HOR, VERT
    }
    
    private class Node {
        public Node leftChild;
        public Node rightChild;
        public Point point;
        public NodeType type;
        public Rectangle rectangle;

        public Node(Node leftChild, Node rightChild, Point point, NodeType type) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.point = point;
            this.type = type;
        }          

        public Node(Point point, NodeType type) {
            this.point = point;
            this.type = type;
        }
        
        public Node() {
            this.leftChild = null;
            this.rightChild = null;
            this.point = null;
        }

        @Override
        public String toString() {
            return point + " t:" + type + " rect: " + rectangle;
        }
        
        public String toStringTree(String separator) {            
            if (leftChild == null && rightChild == null) {
                return this + "\n" + separator + "null" + "\n" + separator + "null";
            } else if (leftChild == null) {
                return this + "\n" + separator + "null"
                    + "\n" + separator + rightChild.toStringTree(separator + " ");                
            } else if (rightChild == null) {
                return this + "\n" + separator + leftChild.toStringTree(separator + " ")
                    + "\n" + separator + "null";
            } else {
                return this + "\n" + separator + leftChild.toStringTree(separator + " ")
                    + "\n" + separator + rightChild.toStringTree(separator + " ");
            }
        }        
    }

    private Node root;
    
    public Tree2d(List<Point> points) {
        if (!points.isEmpty()) {
            this.addRoot(points);
        }
    }
    
    // Selection in O(n). Type: 0 - x, 1 - y
    private Point selection(List<Point> points, int index, int type) {
        List<Integer> list = new ArrayList<Integer>();
        for (Point p : points) {
            if (type == 0) {
                list.add(p.x);
            } else {
                list.add(p.y);
            }
        }
        int value = Selection.select(list, index);
        Point point = null;
        for (Point p : points) {
            if (type == 0) {
                if (p.x == value) {
                    point = p;
                    break;
                }
            } else {
                 if (p.y == value) {
                    point = p;
                    break;
                }
            }
        }
        return point;
    }
    
    private void addRoot(List<Point> points) {

        Collections.sort(points, new Point().getYComparator());
        int y1 = points.get(0).y;
        int y2 = points.get(points.size() - 1).y;
        Collections.sort(points, new Point().getXComparator());
        int x1 = points.get(0).x;
        int x2 = points.get(points.size() - 1).x;
        
        int mid = points.size() / 2;
        Point midPoint = points.get(mid);
        root = new Node(midPoint, NodeType.VERT);
                
        root.rectangle = new Rectangle(x1, x2, y1, y2);
        
        Rectangle leftChildRectangle = new Rectangle(root.rectangle);
        Rectangle rightChildRectangle = new Rectangle(root.rectangle);
        
        leftChildRectangle.x2 = root.point.x;
        rightChildRectangle.x1 = root.point.x;
        
        root.leftChild = getChild(points.subList(0, mid), root, leftChildRectangle);
        root.rightChild = getChild(points.subList(mid + 1, points.size()), root, rightChildRectangle);
    }
    
    private Node getChild(List<Point> points, Node root, Rectangle rectangle) {
        if (points.isEmpty()) {
            return null;
        }
        
        Node node = new Node();
        Point midPoint;
        int mid = points.size() / 2;
        
        if (root.type == NodeType.VERT) {
            midPoint = selection(points, mid, 1);
            node.type = NodeType.HOR;
        } else {
            midPoint = selection(points, mid, 0);
            node.type = NodeType.VERT;
        }
                        
        node.point = midPoint;
        node.rectangle = rectangle;
        
        Rectangle leftChildRectangle = new Rectangle(node.rectangle);
        Rectangle rightChildRectangle= new Rectangle(node.rectangle);
                    
        List<Point> partition = new ArrayList<Point>();
        partition.add(midPoint);
        
        if (node.type == NodeType.VERT) {
            leftChildRectangle.x2 = node.point.x;
            rightChildRectangle.x1 = node.point.x;
            
            for (Point point : points) {
                if (point == midPoint) {
                    continue;
                } else {
                    if (point.x < midPoint.x) {
                        partition.add(0, point);
                    } else {
                        partition.add(partition.size(), point);
                    }
                }
            }
            
        } else {
            leftChildRectangle.y2 = node.point.y;
            rightChildRectangle.y1 = node.point.y;
            
            for (Point point : points) {
                if (point == midPoint) {
                    continue;
                } else {
                    if (point.y < midPoint.y) {
                        partition.add(0, point);
                    } else {
                        partition.add(partition.size(), point);
                    }
                }
            }

        }
        
        int midPointIndex = partition.indexOf(midPoint);
        List<Point> left = partition.subList(0, midPointIndex);
        List<Point> right = partition.subList(midPointIndex + 1, points.size());
        
        node.leftChild = getChild(left, node, leftChildRectangle);
        node.rightChild = getChild(right, node, rightChildRectangle);
        return node;
    }
    
    private List<Point> result;
    
    public List<Point> findPoints(Rectangle rectangle) {
        result = new ArrayList<Point>();
        
        search(root, rectangle);
        
        return result;
    }

    private List<Point> childPoints(Node node) {
        List<Point> points = new ArrayList<Point>();
        Stack<Node> stack = new Stack<Node>();
        stack.push(node);
        
        while (!stack.isEmpty()) {
            Node top = stack.pop();
            if (top.leftChild != null) {
                stack.push(top.leftChild);
            }
            if (top.rightChild != null) {
                stack.push(top.rightChild);
            }
            points.add(top.point);
        }
        
        return points;
    }
    
    private void search(Node node, Rectangle rectangle) {
        
        if (node == null) {
            return;
        }
        
        if (rectangle.contains(node.rectangle)) {
           result.addAll(childPoints(node));
           return;
        }

        if (node.type == NodeType.VERT) {            
            if (node.point.x < rectangle.x1 && node.point.x < rectangle.x2) {
                search(node.rightChild, rectangle);
            }
            if (node.point.x > rectangle.x1 && node.point.x > rectangle.x2) {
                search(node.leftChild, rectangle);
            }
            if (node.point.x >= rectangle.x1 && node.point.x <= rectangle.x2) {
                search(node.leftChild, rectangle);
                search(node.rightChild, rectangle);
                if (node.point.y >= rectangle.y1 && node.point.y <= rectangle.y2) {
                    result.add(node.point);
                }
            }
        } else {
            if (node.point.y < rectangle.y1 && node.point.y < rectangle.y2) {
                search(node.rightChild, rectangle);
            }
            if (node.point.y > rectangle.y1 && node.point.y > rectangle.y2) {
                search(node.leftChild, rectangle);
            }
            if (node.point.y >= rectangle.y1 && node.point.y <= rectangle.y2) {
                search(node.leftChild, rectangle);
                search(node.rightChild, rectangle);
                if (node.point.x >= rectangle.x1 && node.point.x <= rectangle.x2) {
                    result.add(node.point);
                }
            }
        }
    }
        
    @Override
    public String toString() {
        if (root == null) {
            return "Tree2d{" + "\n" + "null" + "\n" + '}';
        } else {
            return "Tree2d{" + "\n" + root.toStringTree(" ") + "\n" + '}';
        }
    }
}

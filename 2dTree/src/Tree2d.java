
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
        public int pointsNumber;

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
        root.pointsNumber = points.size();

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
            Collections.sort(points, new Point.YPointComparator());
            midPoint = selection(points, mid, 1);
            node.type = NodeType.HOR;
        } else {
            Collections.sort(points, new Point.XPointComparator());
            midPoint = selection(points, mid, 0);
            node.type = NodeType.VERT;
        }

        node.point = midPoint;
        node.rectangle = rectangle;
        node.pointsNumber = points.size();

        Rectangle leftChildRectangle = new Rectangle(node.rectangle);
        Rectangle rightChildRectangle = new Rectangle(node.rectangle);

        List<Point> partition = new ArrayList<Point>(points);

        if (node.type == NodeType.VERT) {
            leftChildRectangle.x2 = node.point.x;
            rightChildRectangle.x1 = node.point.x;

            int i = 0;
            int j = partition.size() - 1;
            do {
                while (partition.get(i).x < midPoint.x) ++i;
                while (partition.get(j).x > midPoint.x) --j;
                if (i <= j) {
                    Point temp = partition.get(i);
                    partition.set(i, partition.get(j));
                    partition.set(j, temp);
                    if (partition.get(i).x == partition.get(j).x) {
                        j--;
                    }
                }
            } while (i < j);
        } else {
            leftChildRectangle.y2 = node.point.y;
            rightChildRectangle.y1 = node.point.y;

            int i = 0;
            int j = partition.size() - 1;
            do {
                while (partition.get(i).y < midPoint.y) ++i;
                while (partition.get(j).y > midPoint.y) --j;
                if (i <= j) {
                    Point temp = partition.get(i);
                    partition.set(i, partition.get(j));
                    partition.set(j, temp);
                    if (partition.get(i).y == partition.get(j).y) {
                        j--;
                    }
                }
            } while (i < j);
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
    
    private int searchNumberResult = 0;
    
    public int findPointsNumber(Rectangle rectangle) {
        searchNumberResult = 0;
        searchNumber(root, rectangle);
        return searchNumberResult;
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
    
    private void searchNumber(Node node, Rectangle rectangle) {

        if (node == null) {
            return;
        }

        if (rectangle.contains(node.rectangle)) {
            searchNumberResult += node.pointsNumber;
            return;
        }

        if (node.type == NodeType.VERT) {
            if (node.point.x < rectangle.x1 && node.point.x < rectangle.x2) {
                searchNumber(node.rightChild, rectangle);
            }
            if (node.point.x > rectangle.x1 && node.point.x > rectangle.x2) {
                searchNumber(node.leftChild, rectangle);
            }
            if (node.point.x >= rectangle.x1 && node.point.x <= rectangle.x2) {
                searchNumber(node.leftChild, rectangle);
                searchNumber(node.rightChild, rectangle);
                if (node.point.y >= rectangle.y1 && node.point.y <= rectangle.y2) {
                    searchNumberResult++;
                }
            }
        } else {
            if (node.point.y < rectangle.y1 && node.point.y < rectangle.y2) {
                searchNumber(node.rightChild, rectangle);
            }
            if (node.point.y > rectangle.y1 && node.point.y > rectangle.y2) {
                searchNumber(node.leftChild, rectangle);
            }
            if (node.point.y >= rectangle.y1 && node.point.y <= rectangle.y2) {
                searchNumber(node.leftChild, rectangle);
                searchNumber(node.rightChild, rectangle);
                if (node.point.x >= rectangle.x1 && node.point.x <= rectangle.x2) {
                    searchNumberResult++;
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

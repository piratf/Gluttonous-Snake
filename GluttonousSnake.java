import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

public class GluttonousSnake extends JFrame {
    public static final int Spacing = 15;
    public static final int WIDTH = 15 * 55, HEIGHT = 600;
    public static final int L = 1, R = 2, U = 3, D = 4;

    // 设置初始速度
    public static int SLEEPTIME = 60;

    BufferedImage offersetImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
    // 边框
    public static Rectangle theGreateWall = new Rectangle(Spacing, 3 * Spacing, WIDTH - 2 *Spacing, HEIGHT - (6 * Spacing));
    public static int Gap2Rect = 20;

    Snake snake;
    Node node;

    public GluttonousSnake() {
        snake = new Snake(this);
        createApple();
        this.setBounds(100, 100, WIDTH, HEIGHT);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent arg0) {
                System.out.println(arg0.getKeyCode());
                switch (arg0.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    if (snake.dir == L) {
                        if (SLEEPTIME > 50) {
                            SLEEPTIME -= 10;
                        }
                    } else if (snake.dir == R) {
                        SLEEPTIME += 10;
                    } else {
                        snake.dir = L;
                    }
                    break;
                    case KeyEvent.VK_RIGHT:
                    if (snake.dir == R) {
                        if (SLEEPTIME > 50) {
                            SLEEPTIME -= 10;
                        }
                    } else if (snake.dir == L) {
                        SLEEPTIME += 10;
                    } else {
                        snake.dir = R;
                    }
                    break;
                    case KeyEvent.VK_UP:
                    if (snake.dir == U) {
                        if (SLEEPTIME > 50) {
                            SLEEPTIME -= 10;
                        }
                    } else if (snake.dir == D) {
                        SLEEPTIME += 10;
                    } else {
                        snake.dir = U;
                    }
                    break;
                    case KeyEvent.VK_DOWN:
                    if (snake.dir == D) {
                        if (SLEEPTIME > 50) {
                            SLEEPTIME -= 10;
                        }
                    } else if (snake.dir == U) {
                        SLEEPTIME += 10;
                    } else {
                        snake.dir = D;
                    }
                }
            }
        });
        this.setTitle("Gluttonous Snake");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        new Thread(new ThreadUpadte()).start();
    }

    public void fuck() {
        System.out.println("You are fucked");
        System.exit(0);
    }

    public void paintWall(Graphics2D g2d) {
        g2d.setColor(Color.black);
        g2d.drawRect(theGreateWall.x, theGreateWall.y, theGreateWall.width, theGreateWall.height);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) offersetImage.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        paintWall(g2d);
        if (snake.hitNode(node)) {
            snake.addNode();
            createApple();
        }
        if (snake.hitSelf() || snake.hitWall()) {
            fuck();
        }
        snake.draw(g2d);
        node.draw(g2d);
        g.drawImage(offersetImage, 0, 0, null);
    }

    /**
     * 随机生成一个彩色节点
     * @author piratf
     */
    public void createApple() {
        Color color = getRandomColor();
        int x = (int) (Math.random() * (theGreateWall.width - Gap2Rect) + theGreateWall.x + Gap2Rect),
        y = (int) (Math.random() * (theGreateWall.height - Gap2Rect) + theGreateWall.y + Gap2Rect);
        x = x / Spacing * Spacing;
        y = y / Spacing * Spacing;
        System.out.println(color.toString() + ' ' + x + ' ' + y);
        node = new Node(x, y, color);
    }

    /**
     * 生成一个和背景不同的随机颜色
     * @author piratf
     * @return 节点颜色
     */
    private Color getRandomColor() {
        Color color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
            (int) (Math.random() * 256));
        while (color == this.getBackground())
            color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
                (int) (Math.random() * 256));
        return color;
    }

    public static void main(String args[]) {
        new GluttonousSnake();
    }

    class ThreadUpadte implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(SLEEPTIME);
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class Node {
    int x, y, width = GluttonousSnake.Spacing, height = GluttonousSnake.Spacing;
    public static Color NA_color = Color.black;
    Color color;

    public Node(int x, int y, Color color) {
        this(x, y);
        this.color = color;
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = NA_color;
    }

    public Rectangle getSmallerRect() {
        return new Rectangle(x + 1, y - 1, width - 1, height - 1);
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public boolean hitWall() {
        Rectangle wall = GluttonousSnake.theGreateWall;
        return x < wall.x || y < wall.y || x + width > wall.width + wall.x ||
        y + height > wall.height + wall.y;
    }
}

class Snake {
    public List<Node> nodes = new ArrayList<Node>();
    GluttonousSnake interFace;
    int dir = GluttonousSnake.R;

    public Snake(GluttonousSnake interFace) {
        this.interFace = interFace;
        nodes.add(new Node(30 + 180, 60 + 180));
        addNode();
    }

    /**
     * 自己和自己撞
     * @author piratf
     * @return true if hit
     */
    public boolean hitSelf() {
        if (nodes.size() > 5) {
            for (Node node1 : nodes) {
                for (Node node2 : nodes) {
                    if (node2 != node1 && node1.getSmallerRect().intersects(node2.getSmallerRect())) {
                        System.out.println("you hit yourself!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断碰撞
     * @author piratf
     * @param  node 起始节点
     * @return      1: 节点和蛇碰撞
     *              0: 节点没有和蛇碰撞
     */
    public boolean hitNode(Node node) {
        for (Node node1 : nodes) {
            if (node1.getRect().intersects(node.getRect())) {
                System.out.println("Bingo!");
                return true;
            }
        }
        return false;
    }

    public boolean hitWall() {
        for (Node node : nodes) {
            if (node.hitWall()) {
                System.out.println("got the wall!");
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2d) {
        for (Node node : nodes) {
            node.draw(g2d);
        }
        move();
    }

    public void move() {
        nodes.remove((nodes.size() - 1));
        addNode();
    }

    public synchronized void addNode() {
        Node nodeTempNode = nodes.get(0);
        switch (dir) {
            case GluttonousSnake.L:
            if (nodeTempNode.x <= 20) {
                nodeTempNode = new Node(20 + 15 * 50, nodeTempNode.y);
            }
            nodes.add(0, new Node(nodeTempNode.x - nodeTempNode.width,
                nodeTempNode.y));
            break;
            case GluttonousSnake.R:
            if (nodeTempNode.x >= 20 + 15 * 50 - nodeTempNode.width) {
                nodeTempNode = new Node(20 - nodeTempNode.width, nodeTempNode.y);
            }
            nodes.add(0, new Node(nodeTempNode.x + nodeTempNode.width,
                nodeTempNode.y));
            break;
            case GluttonousSnake.U:
            if (nodeTempNode.y <= 40) {
                nodeTempNode = new Node(nodeTempNode.x, 40 + 15 * 35);
            }
            nodes.add(0, new Node(nodeTempNode.x, nodeTempNode.y - nodeTempNode.height));
            break;
            case GluttonousSnake.D:
            if (nodeTempNode.y >= 40 + 15 * 35 - nodeTempNode.height) {
                nodeTempNode = new Node(nodeTempNode.x, 40 - nodeTempNode.height);
            }
            nodes.add(0, new Node(nodeTempNode.x, nodeTempNode.y + nodeTempNode.height));
            break;
        }
    }
}
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
    public static final int WIDTH = 800, HEIGHT = 600;
    public static final L = 1, R = 2, U = 3, D = 4;
    public static int SLEEPTIME = 60;
    BufferedImage offersetImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
    public static Rectangle rect = new Rectangle(20, 40, 15 * 50, 15 * 35);
    Snake snake;
    Node node;

    public GluttonousSnake() {
        snake = new Snake(this);
        createNode();
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

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) offersetImage.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.black);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
        int result = snake.hit(node);
        if (result == 1) {
            createNode();
        }
        if (snake.hitWall() || result == -1) {
            fuck();
        }
        snake.draw(g2d);
        node.draw(g2d);
        g.drawImage(offersetImage, 0, 0, null);
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

    public void createNode() {
        Color color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
                (int) (Math.random() * 256));
        while (color == this.getBackground())
            color = new Color((int) (Math.random() * 256), (int) (Math.random() * 256),
                    (int) (Math.random() * 256));
        int x = (int) (Math.random() * (rect.width) + rect.x),
                y = (int) (Math.random() * (rect.height) + rect.y);
        System.out.println(color.toString() + ' ' + x + ' ' + y);
        node = new Node(x, y, color);
    }

    public static void main(String args[]) {
        new GluttonousSnake();
    }
}

class Node {
    int x, y, width = 15, height = 15;
    Color color;

    public Node(int x, int y, Color color) {
        this(x, y);
        this.color = color;
    }

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.black;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public boolean hitWall() {
        return x <= 0 || y <= 0 || x + width >= GluttonousSnake.rect.width + GluttonousSnake.rect.x ||
                y + height >= GluttonousSnake.rect.height + GluttonousSnake.rect.y;
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

    public int hit(Node node) {
        for (Node node1 : nodes) {
            if (node1.getRect().intersects(node.getRect())) {
                addNode();
                return 1;
            }
            if (nodes.size() > 5) {
                for (Node node2 : nodes) {
                    if (new Rectangle(node1.getRect().width-1, node1.getRect
                            ().height-1)
                            .intersects(node2
                            .getRect())) {
                        return -1;
                    }
                }
            }
        }
        return 0;
    }

    public boolean hitWall() {
        for (Node node : nodes) {
            if (node.hitWall()) {
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
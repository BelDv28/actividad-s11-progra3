package umg.actividad;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel que dibuja visualmente un árbol binario decodificado.
 * Las hojas se muestran en verde, los nodos internos en azul,
 * y los nodos vacíos (.) como círculos punteados pequeños.
 */
public class TreePanel extends JPanel {

    // Colores consistentes con MainGUI
    private static final Color BG             = new Color(26,  26,  26);
    private static final Color LEAF_FILL      = new Color(29, 158, 117);   // verde
    private static final Color LEAF_BORDER    = new Color(15, 110,  86);
    private static final Color LEAF_TEXT      = new Color(225, 245, 238);
    private static final Color NODE_FILL      = new Color(55, 138, 221);   // azul
    private static final Color NODE_BORDER    = new Color(24,  95, 165);
    private static final Color NODE_TEXT      = new Color(230, 241, 251);
    private static final Color EMPTY_BORDER   = new Color(80,  80,  80);
    private static final Color EMPTY_TEXT     = new Color(100, 100, 100);
    private static final Color EDGE_COLOR     = new Color(70,  70,  70);
    private static final Color EDGE_EMPTY     = new Color(50,  50,  50);
    private static final Color RESULT_COLOR   = new Color(130, 190, 130);
    private static final Color HINT_COLOR     = new Color(80,  80,  80);

    private static final Font FONT_NODE   = new Font("Consolas", Font.BOLD,  15);
    private static final Font FONT_EMPTY  = new Font("Consolas", Font.PLAIN, 11);
    private static final Font FONT_RESULT = new Font("Segoe UI", Font.PLAIN, 13);

    private static final int NODE_R     = 18;   // radio nodos principales
    private static final int EMPTY_R    = 8;    // radio nodos vacíos
    private static final int LEVEL_H    = 75;   // altura entre niveles
    private static final int PADDING    = 40;   // margen exterior

    // Nodo interno del árbol para dibujo
    private static class TreeNode {
        char   ch;
        boolean empty;
        boolean isLeaf;
        TreeNode left, right;
        // coordenadas calculadas en layout
        int x, y;
    }

    private TreeNode root       = null;
    private String   decoded    = "";
    private int      treeWidth  = 600;
    private int      treeHeight = 200;

    public TreePanel() {
        setBackground(BG);
        setPreferredSize(new Dimension(600, 300));
    }

    // ──────────────────────────────────────────────
    // API pública
    // ──────────────────────────────────────────────

    /** Parsea el string codificado y recalcula el layout. */
    public void setTree(String encoded, ArbolDecoder decoder) {
        pos = 0;
        root = parse(encoded);
        decoded = decoder.decodificar(encoded);
        relayout();
        repaint();
    }

    public void clear() {
        root    = null;
        decoded = "";
        repaint();
    }

    // ──────────────────────────────────────────────
    // Parseo recursivo (mismo algoritmo que ArbolDecoder)
    // ──────────────────────────────────────────────

    private int pos;

    private TreeNode parse(String s) {
        if (pos >= s.length()) return emptyNode();
        char c = s.charAt(pos++);
        if (c == '.') return emptyNode();

        TreeNode n = new TreeNode();
        n.ch    = c;
        n.empty = false;
        n.left  = parse(s);
        n.right = parse(s);
        n.isLeaf = n.left.empty && n.right.empty;
        return n;
    }

    private TreeNode emptyNode() {
        TreeNode n = new TreeNode();
        n.empty  = true;
        n.isLeaf = false;
        return n;
    }

    // ──────────────────────────────────────────────
    // Layout: asigna coordenadas a cada nodo
    // ──────────────────────────────────────────────

    private void relayout() {
        if (root == null || root.empty) {
            treeWidth  = 600;
            treeHeight = 120;
            setPreferredSize(new Dimension(treeWidth, treeHeight));
            return;
        }

        // 1. Contar hojas para calcular ancho mínimo
        int leafCount = countLeaves(root);
        int minWidth  = Math.max(600, leafCount * 55 + PADDING * 2);

        // 2. Calcular profundidad para el alto
        int depth = depth(root);
        treeHeight = PADDING + depth * LEVEL_H + PADDING + 30;
        treeWidth  = minWidth;

        // 3. Asignar posiciones
        assignPositions(root, 0, PADDING, treeWidth - PADDING, PADDING + NODE_R);

        setPreferredSize(new Dimension(treeWidth, treeHeight));
        revalidate();
    }

    private void assignPositions(TreeNode n, int depth, int xMin, int xMax, int y) {
        if (n == null) return;
        n.x = (xMin + xMax) / 2;
        n.y = y;
        int nextY = y + LEVEL_H;
        if (!n.empty) {
            assignPositions(n.left,  depth + 1, xMin,   n.x, nextY);
            assignPositions(n.right, depth + 1, n.x, xMax,   nextY);
        }
    }

    private int countLeaves(TreeNode n) {
        if (n == null || n.empty) return 0;
        if (n.isLeaf) return 1;
        return countLeaves(n.left) + countLeaves(n.right);
    }

    private int depth(TreeNode n) {
        if (n == null || n.empty) return 0;
        return 1 + Math.max(depth(n.left), depth(n.right));
    }

    // ──────────────────────────────────────────────
    // Pintado
    // ──────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (root == null || root.empty) {
            drawHint(g2);
            return;
        }

        // Dibujar aristas primero (quedan debajo de los nodos)
        drawEdges(g2, root);

        // Dibujar nodos
        drawNodes(g2, root);

        // Resultado al pie
        drawResult(g2);
    }

    private void drawHint(Graphics2D g2) {
        g2.setFont(FONT_RESULT);
        g2.setColor(HINT_COLOR);
        String msg = "Ingresa un mensaje y presiona Visualizar";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth()  - fm.stringWidth(msg)) / 2;
        int y = (getHeight() + fm.getAscent())       / 2;
        g2.drawString(msg, x, y);
    }

    private void drawEdges(Graphics2D g2, TreeNode n) {
        if (n == null || n.empty) return;

        Stroke solid  = new BasicStroke(1.2f);
        Stroke dashed = new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{4, 4}, 0f);

        if (n.left != null) {
            boolean emptyChild = n.left.empty;
            g2.setStroke(emptyChild ? dashed : solid);
            g2.setColor(emptyChild ? EDGE_EMPTY : EDGE_COLOR);
            g2.drawLine(n.x, n.y, n.left.x, n.left.y);
            drawEdges(g2, n.left);
        }
        if (n.right != null) {
            boolean emptyChild = n.right.empty;
            g2.setStroke(emptyChild ? dashed : solid);
            g2.setColor(emptyChild ? EDGE_EMPTY : EDGE_COLOR);
            g2.drawLine(n.x, n.y, n.right.x, n.right.y);
            drawEdges(g2, n.right);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawNodes(Graphics2D g2, TreeNode n) {
        if (n == null) return;

        if (n.empty) {
            // Círculo pequeño punteado
            int r = EMPTY_R;
            g2.setColor(BG);
            g2.fillOval(n.x - r, n.y - r, r * 2, r * 2);
            Stroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{3, 3}, 0f);
            g2.setStroke(dashed);
            g2.setColor(EMPTY_BORDER);
            g2.drawOval(n.x - r, n.y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(FONT_EMPTY);
            g2.setColor(EMPTY_TEXT);
            drawCenteredString(g2, "·", n.x, n.y);
            return;
        }

        int r = NODE_R;

        if (n.isLeaf) {
            // Hoja → verde con sombra suave
            g2.setColor(LEAF_FILL);
            g2.fillOval(n.x - r, n.y - r, r * 2, r * 2);
            g2.setColor(LEAF_BORDER);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(n.x - r, n.y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(FONT_NODE);
            g2.setColor(LEAF_TEXT);
        } else {
            // Nodo interno → azul
            g2.setColor(NODE_FILL);
            g2.fillOval(n.x - r, n.y - r, r * 2, r * 2);
            g2.setColor(NODE_BORDER);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(n.x - r, n.y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(FONT_NODE);
            g2.setColor(NODE_TEXT);
        }

        drawCenteredString(g2, String.valueOf(n.ch), n.x, n.y);

        // Recursión
        drawNodes(g2, n.left);
        drawNodes(g2, n.right);
    }

    private void drawResult(Graphics2D g2) {
        g2.setFont(FONT_RESULT);
        g2.setColor(HINT_COLOR);
        String label = "Mensaje descifrado: ";
        FontMetrics fm = g2.getFontMetrics();
        int totalW = fm.stringWidth(label) + fm.stringWidth(decoded);
        int x = (getWidth() - totalW) / 2;
        int y = treeHeight - 14;
        g2.drawString(label, x, y);
        g2.setColor(RESULT_COLOR);
        g2.drawString(decoded, x + fm.stringWidth(label), y);
    }

    private void drawCenteredString(Graphics2D g2, String s, int cx, int cy) {
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(s) / 2;
        int y = cy + fm.getAscent() / 2 - 1;
        g2.drawString(s, x, y);
    }
}
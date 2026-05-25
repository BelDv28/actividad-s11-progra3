package umg.actividad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class MainGUI extends JFrame {

    private static final Color BG_DARK      = new Color(18,  18,  18);
    private static final Color BG_CARD      = new Color(26,  26,  26);
    private static final Color BG_INPUT     = new Color(34,  34,  34);
    private static final Color ACCENT       = new Color(99, 149, 210);
    private static final Color SUCCESS      = new Color(130, 190, 130);
    private static final Color ERROR_COLOR  = new Color(200, 100, 100);
    private static final Color TEXT_MAIN    = new Color(220, 220, 220);
    private static final Color TEXT_MUTED   = new Color(120, 120, 120);
    private static final Color BORDER_COLOR = new Color(48,  48,  48);

    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.PLAIN,  20);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN,  12);
    private static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD,   12);
    private static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 14);
    private static final Font FONT_MONO_SM = new Font("Consolas",  Font.PLAIN, 12);
    private static final Font FONT_RESULT  = new Font("Segoe UI",  Font.PLAIN, 34);
    private static final Font FONT_SMALL   = new Font("Segoe UI",  Font.PLAIN, 11);

    private JTextField        inputField;
    private JTextArea         batchArea;
    private JLabel            resultLabel;
    private JLabel            statusLabel;
    private JPanel            resultPanel;
    private DefaultTableModel tableModel;
    private DefaultTableModel historyModel;
    private JTabbedPane       tabbedPane;
    private TreePanel treePanel;

    private final ArbolDecoder decoder = new ArbolDecoder();

    public MainGUI() {
        super("Decodificador de Árbol Binario");
        setupFrame();
        buildUI();
        setVisible(true);
    }

    private void setupFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(780, 640);
        setMinimumSize(new Dimension(680, 540));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(24, 28, 20, 28));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(BG_DARK);

        JLabel title = new JLabel("Decodificador de Árbol Binario");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("TAREA 9 PROGRA 3");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(sub);

        JLabel badge = new JLabel("UNIVERSIDAD MARIANO GALVEZ");
        badge.setFont(FONT_SMALL);
        badge.setForeground(TEXT_MUTED);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(3, 10, 3, 10)));

        p.add(titles, BorderLayout.WEST);
        p.add(badge,  BorderLayout.EAST);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.add(p,   BorderLayout.CENTER);
        wrapper.add(sep, BorderLayout.SOUTH);
        return wrapper;
    }

    private JComponent buildCenter() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_MUTED);
        tabbedPane.setFont(FONT_LABEL);
        tabbedPane.setBorder(new EmptyBorder(14, 0, 0, 0));
        tabbedPane.addTab("Decodificador", buildSingleTab());
        tabbedPane.addTab("Árbol", buildTreeTab());
        tabbedPane.addTab("Lote",          buildBatchTab());
        tabbedPane.addTab("Historial",     buildHistoryTab());
        return tabbedPane;
    }

    private JPanel buildSingleTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Card entrada
        JPanel inputCard = makeCard();
        inputCard.setLayout(new BorderLayout(0, 12));
        inputCard.add(makeLabel("Mensaje cifrado"), BorderLayout.NORTH);

        inputField = new JTextField();
        styleTextField(inputField);
        inputField.setToolTipText("Ej: xb..zu..t.u..");
        inputField.addActionListener(e -> decode());
        inputCard.add(inputField, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setBackground(BG_CARD);
        JLabel exLbl = new JLabel("Ejemplos:");
        exLbl.setFont(FONT_SMALL);
        exLbl.setForeground(TEXT_MUTED);
        btnRow.add(exLbl);
        btnRow.add(makeExampleButton("xb..zu..t.u..",     "buu"));
        btnRow.add(makeExampleButton("abh...ko..nl..a..", "hola"));
        btnRow.add(makeExampleButton("a..",               "a"));
        inputCard.add(btnRow, BorderLayout.SOUTH);

        // Card resultado
        resultPanel = makeCard();
        resultPanel.setLayout(new BorderLayout(0, 8));
        resultPanel.setPreferredSize(new Dimension(0, 155));
        resultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));

        resultLabel = new JLabel("—", SwingConstants.CENTER);
        resultLabel.setFont(FONT_RESULT);
        resultLabel.setForeground(new Color(60, 60, 60));

        statusLabel = new JLabel("Ingresa un mensaje y presiona Decodificar", SwingConstants.CENTER);
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_MUTED);

        JPanel inner = new JPanel(new BorderLayout(0, 4));
        inner.setBackground(BG_CARD);
        inner.add(makeLabel("Resultado"), BorderLayout.NORTH);
        inner.add(resultLabel,            BorderLayout.CENTER);
        inner.add(statusLabel,            BorderLayout.SOUTH);
        resultPanel.add(inner, BorderLayout.CENTER);

        // Botones
        JButton btn      = makeAccentButton("Decodificar");
        btn.addActionListener(e -> decode());

        JButton clearBtn = makeGhostButton("Limpiar");
        clearBtn.addActionListener(e -> clearResult());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(BG_DARK);
        btnPanel.add(btn);
        btnPanel.add(clearBtn);

        p.add(inputCard);
        p.add(Box.createVerticalStrut(12));
        p.add(resultPanel);
        p.add(Box.createVerticalStrut(16));
        p.add(btnPanel);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JPanel buildBatchTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel topCard = makeCard();
        topCard.setLayout(new BorderLayout(0, 10));
        topCard.add(makeLabel("Un mensaje por línea:"), BorderLayout.NORTH);

        batchArea = new JTextArea(6, 30);
        batchArea.setBackground(BG_INPUT);
        batchArea.setForeground(TEXT_MAIN);
        batchArea.setCaretColor(TEXT_MUTED);
        batchArea.setFont(FONT_MONO_SM);
        batchArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        batchArea.setText("abh...ko..nl..a..\nxb..zu..t.u..\na..\nab..c..");

        JScrollPane scroll = new JScrollPane(batchArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.setBackground(BG_INPUT);
        topCard.add(scroll, BorderLayout.CENTER);

        JButton btnBatch = makeAccentButton("Procesar todo");
        btnBatch.addActionListener(e -> processBatch());
        JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bRow.setBackground(BG_CARD);
        bRow.add(btnBatch);
        topCard.add(bRow, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"#", "Entrada", "Salida"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(36);
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        cr.setForeground(TEXT_MUTED);
        table.getColumnModel().getColumn(0).setCellRenderer(cr);

        JScrollPane ts = new JScrollPane(table);
        ts.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JPanel tableCard = makeCard();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.add(makeLabel("Resultados:"), BorderLayout.NORTH);
        tableCard.add(ts, BorderLayout.CENTER);

        p.add(topCard,   BorderLayout.NORTH);
        p.add(tableCard, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildHistoryTab() {
        historyModel = new DefaultTableModel(new String[]{"Entrada", "Resultado", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable ht = styledTable(historyModel);
        ht.getColumnModel().getColumn(2).setMaxWidth(65);
        ht.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setForeground("OK".equals(val) ? SUCCESS : ERROR_COLOR);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(BG_INPUT);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(ht);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JButton clr = makeGhostButton("Limpiar historial");
        clr.addActionListener(e -> historyModel.setRowCount(0));
        JPanel bRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bRow.setBackground(BG_CARD);
        bRow.add(clr);

        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 8));
        card.add(makeLabel("Historial:"), BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(bRow,   BorderLayout.SOUTH);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 0, 0, 0));
        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(12, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);

        JLabel info = new JLabel("INGENIERIA EN SISTEMAS PROGRA 3 SECC B");
        info.setFont(FONT_SMALL);
        info.setForeground(new Color(70, 70, 70));
        info.setBorder(new EmptyBorder(8, 0, 0, 0));

        p.add(sep,  BorderLayout.NORTH);
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    // Logica del sistema

    private void decode() {
        String raw = inputField.getText().trim();
        if (raw.isEmpty()) { shake(inputField); return; }
        try {
            String result = decoder.decodificar(raw);
            resultLabel.setFont(FONT_RESULT);
            resultLabel.setForeground(TEXT_MAIN);
            resultLabel.setText(result);
            statusLabel.setForeground(TEXT_MUTED);
            statusLabel.setText("Hojas: " + result.length()
                    + "  ·  Entrada: " + raw.length() + " chars");
            resultPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    new EmptyBorder(14, 16, 14, 16)));
            historyModel.insertRow(0, new Object[]{raw, result, "OK"});
            treePanel.setTree(raw, decoder);
        } catch (Exception ex) {
            resultLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            resultLabel.setForeground(ERROR_COLOR);
            resultLabel.setText(ex.getMessage());
            statusLabel.setText("");
            resultPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ERROR_COLOR, 1),
                    new EmptyBorder(14, 16, 14, 16)));
            historyModel.insertRow(0, new Object[]{raw, ex.getMessage(), "ERR"});
        }
    }

    private void clearResult() {
        inputField.setText("");
        resultLabel.setText("—");
        resultLabel.setFont(FONT_RESULT);
        resultLabel.setForeground(new Color(60, 60, 60));
        treePanel.clear();
        statusLabel.setText("Ingresa un mensaje y presiona Decodificar");
        resultPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)));
    }

    private void processBatch() {
        tableModel.setRowCount(0);
        String[] lines = batchArea.getText().split("\\n");
        int i = 1;
        for (String line : lines) {
            String l = line.trim();
            if (l.isEmpty()) continue;
            try {
                String r = decoder.decodificar(l);
                tableModel.addRow(new Object[]{i++, l, r});
                historyModel.insertRow(0, new Object[]{l, r, "OK"});
            } catch (Exception ex) {
                tableModel.addRow(new Object[]{i++, l, "ERR: " + ex.getMessage()});
                historyModel.insertRow(0, new Object[]{l, ex.getMessage(), "ERR"});
            }
        }
        tabbedPane.setSelectedIndex(2);
    }

    // HELPERS

    private JPanel makeCard() {
        JPanel c = new JPanel();
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 16, 14, 16)));
        return c;
    }

    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void styleTextField(JTextField f) {
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(TEXT_MUTED);
        f.setFont(FONT_MONO);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(8, 12, 8, 12)));
    }

    private JButton makeAccentButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(new Color(255, 255, 255));
        b.setFont(FONT_BOLD);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(9, 24, 9, 24));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(ACCENT.darker());
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(ACCENT);
            }
        });
        return b;
    }

    private JButton makeGhostButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BG_DARK);
        b.setForeground(TEXT_MUTED);
        b.setFont(FONT_LABEL);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(9, 24, 9, 24)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setForeground(TEXT_MAIN);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                        new EmptyBorder(9, 24, 9, 24)));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setForeground(TEXT_MUTED);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(9, 24, 9, 24)));
            }
        });
        return b;
    }

    private JButton makeExampleButton(String input, String hint) {
        JButton b = new JButton(input + " → " + hint);
        b.setBackground(BG_INPUT);
        b.setForeground(TEXT_MUTED);
        b.setFont(FONT_MONO_SM);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(3, 8, 3, 8)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> { inputField.setText(input); decode(); });
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(TEXT_MAIN); }
            @Override public void mouseExited(MouseEvent e)  { b.setForeground(TEXT_MUTED); }
        });
        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_INPUT);
        t.setForeground(TEXT_MAIN);
        t.setFont(FONT_MONO_SM);
        t.setRowHeight(26);
        t.setGridColor(BORDER_COLOR);
        t.setShowGrid(true);
        t.getTableHeader().setBackground(BG_CARD);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(FONT_SMALL);
        t.setSelectionBackground(new Color(50, 50, 50));
        t.setSelectionForeground(TEXT_MAIN);
        return t;
    }

    private void shake(JComponent comp) {
        Point orig = comp.getLocation();
        Timer timer = new Timer(28, null);
        int[] seq = {-7, 7, -5, 5, -3, 3, 0};
        int[] idx = {0};
        timer.addActionListener(e -> {
            comp.setLocation(orig.x + seq[idx[0]], orig.y);
            if (++idx[0] >= seq.length) { comp.setLocation(orig); timer.stop(); }
        });
        timer.start();
    }
    private JPanel buildTreeTab() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG_DARK);
        p.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel topCard = makeCard();
        topCard.setLayout(new BorderLayout(0, 10));
        topCard.add(makeLabel("El árbol se actualiza automáticamente al decodificar. (Funciona solo para 1 palabra a la vez)"), BorderLayout.NORTH);

        treePanel = new TreePanel();

        JScrollPane scroll = new JScrollPane(treePanel);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(new Color(26, 26, 26));

        p.add(topCard, BorderLayout.NORTH);
        p.add(scroll,  BorderLayout.CENTER);
        return p;
    }

    // MAIN

    public static void main(String[] args) {
        try {
            Class<?> flatDark = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            flatDark.getMethod("setup").invoke(null);
            UIManager.put("TabbedPane.underlineColor",         ACCENT);
            UIManager.put("TabbedPane.inactiveUnderlineColor", BORDER_COLOR);
            UIManager.put("TabbedPane.selectedBackground",     BG_CARD);
            UIManager.put("TabbedPane.selectedForeground",     new Color(220, 220, 220));
            UIManager.put("ScrollBar.width", 6);
            UIManager.put("ScrollBar.thumbColor", new Color(60, 60, 60));
        } catch (Exception ignored) {
            try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
            catch (Exception e2) { /* default */ }
        }
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
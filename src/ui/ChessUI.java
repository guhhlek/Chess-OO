package ui;

import ai.*;
import model.*;

import javax.swing.*;
import java.awt.*;

public class ChessUI extends JFrame {
    private JButton[][] buttons = new JButton[8][8];
    private Board board;
    private Position selected = null;
    private JLabel turnLabel;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);
    private boolean vsAI = false;
    private ChessAI aiPlayer;

    public ChessUI() {
        this(new Board());
    }

    public ChessUI(Board board) {
        super("♟️ Xadrez");
        this.board = board;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        initTurnLabel();
        showStartupMenu();
        initBottomPanel();
        initBoardGrid();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refresh();
            }
        });

        setVisible(true);
    }

    private void initTurnLabel() {
        turnLabel = new JLabel("Vez das Brancas", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setOpaque(true);
        turnLabel.setBackground(Color.LIGHT_GRAY);
        turnLabel.setPreferredSize(new Dimension(100, 40));
        add(turnLabel, BorderLayout.NORTH);
    }

    private void showStartupMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel label = new JLabel("Deseja jogar contra a IA?");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(new Color(50, 50, 50));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        panel.add(label);

        JButton vsAIButton = new JButton("Sim");
        JButton manualButton = new JButton("Não");

        vsAIButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        vsAIButton.setFont(new Font("Arial", Font.PLAIN, 18));
        manualButton.setFont(new Font("Arial", Font.PLAIN, 18));

        vsAIButton.setBackground(new Color(100, 200, 100));
        vsAIButton.setForeground(Color.WHITE);
        manualButton.setBackground(new Color(200, 100, 100));
        manualButton.setForeground(Color.WHITE);

        vsAIButton.setFocusPainted(false);
        manualButton.setFocusPainted(false);

        panel.add(vsAIButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(manualButton);

        JDialog dialog = new JDialog(this, "Modo de Jogo", true);
        dialog.setContentPane(panel);
        dialog.setMinimumSize(new Dimension(400, 250));
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        vsAIButton.addActionListener(e -> {
            vsAI = true;
            dialog.dispose();
            showDifficultyMenu();
        });

        manualButton.addActionListener(e -> {
            vsAI = false;
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void showDifficultyMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel label = new JLabel("Selecione a dificuldade:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(new Color(50, 50, 50));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        panel.add(label);

        JButton easyBtn = new JButton("Fácil");
        JButton mediumBtn = new JButton("Médio");
        JButton hardBtn = new JButton("Difícil");

        JButton[] buttons = { easyBtn, mediumBtn, hardBtn };
        Color[] colors = { new Color(100, 200, 100), new Color(255, 165, 0), new Color(200, 50, 50) };

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.setBackground(colors[i]);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JDialog dialog = new JDialog(this, "Dificuldade da IA", true);
        dialog.setContentPane(panel);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        easyBtn.addActionListener(e -> {
            aiPlayer = new EasyAI(ChessColor.BLACK);
            dialog.dispose();
        });

        mediumBtn.addActionListener(e -> {
            aiPlayer = new MediumAI(ChessColor.BLACK);
            dialog.dispose();
        });

        hardBtn.addActionListener(e -> {
            aiPlayer = new HardAI(ChessColor.BLACK);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton undoBtn = new JButton("↩️ Desfazer");
        undoBtn.addActionListener(e -> {
            if (board.undo())
                refresh();
        });
        bottomPanel.add(undoBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initBoardGrid() {
        JPanel panel = new JPanel(new GridLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = new JButton();
                btn.setFont(new Font("SansSerif", Font.PLAIN, 36));
                btn.setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
                final int row = r, col = c;
                btn.addActionListener(e -> handleClick(row, col));
                buttons[r][c] = btn;
                panel.add(btn);
            }
        }

        add(panel, BorderLayout.CENTER);
        refresh();
    }

    private void handleClick(int row, int col) {
        Piece p = board.at(row, col);

        if (selected == null) {
            if (p == null)
                return;

            if (p.getColor() != board.getSideToMove()) {
                JOptionPane.showMessageDialog(this,
                        "É a vez das " + (board.getSideToMove() == ChessColor.WHITE ? "Brancas" : "Pretas") + "!");
                return;
            }

            selected = new Position(row, col);
            buttons[row][col].setBackground(new Color(235, 240, 139));

            for (Position move : p.legalTargets(board, selected)) {
                buttons[move.row][move.col].setBackground(new Color(182, 245, 182));
            }
        } else {
            Position to = new Position(row, col);
            Position from = selected;

            boolean moved = board.move(from, to);

            resetColors();
            selected = null;

            if (moved) {
                animateMove(from, to, p);
                refresh();

                turnLabel.setText("Vez das " + (board.getSideToMove() == ChessColor.WHITE ? "Brancas" : "Pretas"));
                turnLabel.setBackground(
                        board.getSideToMove() == ChessColor.WHITE ? new Color(200, 230, 255)
                                : new Color(255, 200, 200));

                if (board.inCheck(board.getSideToMove())) {
                    if (board.isCheckmate(board.getSideToMove())) {
                        JOptionPane.showMessageDialog(this,
                                "Cheque-mate! " +
                                        (board.getSideToMove() == ChessColor.WHITE ? "Pretas" : "Brancas") +
                                        " venceram!");

                        board = new Board();
                        refresh();
                        turnLabel.setText("Vez das Brancas");
                        turnLabel.setBackground(Color.LIGHT_GRAY);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Cheque nas " +
                                        (board.getSideToMove() == ChessColor.WHITE ? "Brancas" : "Pretas") + "!");
                    }
                }

            } else {
                if (board.inCheck(board.getSideToMove())) {
                    JOptionPane.showMessageDialog(this,
                            "Você não pode mover essa peça! Seu rei ficaria em cheque.");
                }
            }
        }

        if (vsAI && board.getSideToMove() == ChessColor.BLACK) {
            makeAIMove();
        }
    }

    private void resetColors() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                buttons[r][c].setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
    }

    private void animateMove(Position from, Position to, Piece piece) {
        JButton fromBtn = buttons[from.row][from.col];
        JButton toBtn = buttons[to.row][to.col];

        Icon icon = fromBtn.getIcon();
        fromBtn.setIcon(null);

        Timer timer = new Timer(200, e -> {
            toBtn.setIcon(icon);
            ((Timer) e.getSource()).stop();
        });
        timer.start();
    }

    private Icon getPieceIcon(Piece p, JButton btn) {
        if (p == null)
            return null;

        String name = switch (p.getClass().getSimpleName()) {
            case "King" -> "king";
            case "Queen" -> "queen";
            case "Rook" -> "rook";
            case "Bishop" -> "bishop";
            case "Knight" -> "knight";
            case "Pawn" -> "pawn";
            default -> "";
        };

        String path = "images/" + (p.getColor() == ChessColor.WHITE ? "white_" : "black_") + name + ".png";
        ImageIcon icon = new ImageIcon(path);

        int w = btn.getWidth();
        int h = btn.getHeight();
        if (w <= 0 || h <= 0)
            return icon;

        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void refresh() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                buttons[r][c].setIcon(getPieceIcon(board.at(r, c), buttons[r][c]));

        for (ChessColor color : ChessColor.values()) {
            if (board.inCheck(color)) {
                Position kingPos = board.findKing(color);
                if (kingPos != null)
                    buttons[kingPos.row][kingPos.col].setBackground(Color.RED);
            }
        }
    }

    private void updateTurnLabel() {
        turnLabel.setText("Vez das " + (board.getSideToMove() == ChessColor.WHITE ? "Brancas" : "Pretas"));
        turnLabel.setBackground(
                board.getSideToMove() == ChessColor.WHITE ? new Color(200, 230, 255) : new Color(255, 200, 200));
    }

    private void makeAIMove() {
        if (aiPlayer == null)
            return;

        Move move = aiPlayer.chooseMove(board);
        if (move == null)
            return;

        Piece p = board.at(move.from.row, move.from.col);
        board.move(move.from, move.to);

        animateMove(move.from, move.to, p);
        refresh();
        updateTurnLabel();
    }
}

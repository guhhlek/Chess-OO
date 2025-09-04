package ui;

import ai.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessUI extends JFrame {
    private JButton[][] buttons = new JButton[8][8];
    private Board board;
    private Position selected = null;
    private JLabel turnLabel;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);
    private boolean vsAI = false;
    private JButton aiBtn;
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

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton undoBtn = new JButton("↩️ Desfazer");
        undoBtn.addActionListener(e -> {
            if (board.undo())
                refresh();
        });
        bottomPanel.add(undoBtn);

        JButton easyBtn = new JButton("🤖 Fácil");
        easyBtn.addActionListener(e -> aiPlayer = new EasyAI(ChessColor.BLACK));
        bottomPanel.add(easyBtn);

        JButton mediumBtn = new JButton("🤖 Médio");
        mediumBtn.addActionListener(e -> aiPlayer = new MediumAI(ChessColor.BLACK));
        bottomPanel.add(mediumBtn);

        aiBtn = new JButton("🤖 Ativar IA");
        aiBtn.addActionListener(e -> {
            vsAI = !vsAI;
            aiBtn.setText(vsAI ? "🤖 IA Ativada" : "🤖 Ativar IA");
        });
        bottomPanel.add(aiBtn);

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
                        "Não, é a vez das " +
                                (board.getSideToMove() == ChessColor.WHITE ? "Brancas" : "Pretas") + "!");
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

                if (board.inCheck(board.getSideToMove())) {
                    JOptionPane.showMessageDialog(this,
                            "Você está em cheque! Só pode mover peças que tirem o rei do cheque.");
                }
            }
        }

        if (vsAI && board.getSideToMove() == ChessColor.BLACK) {
            makeAIMove();
        }
    }

    private void highlightSelectedAndMoves(Position selected, List<Position> moves) {
        buttons[selected.row][selected.col].setBackground(new Color(235, 240, 139));
        for (Position move : moves) {
            buttons[move.row][move.col].setBackground(new Color(182, 245, 182));
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

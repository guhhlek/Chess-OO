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
    private JButton aiBtn;

    public ChessUI() {
        this(new Board());
    }

    public ChessUI(Board b) {
        super("♟️ Xadrez");
        this.board = b;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        turnLabel = new JLabel("Vez das Brancas", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setOpaque(true);
        turnLabel.setBackground(Color.LIGHT_GRAY);
        turnLabel.setPreferredSize(new Dimension(100, 40));
        add(turnLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton undoBtn = new JButton("↩️ Desfazer");
        undoBtn.addActionListener(e -> {
            if (board.undo()) {
                refresh();
            }
        });
        bottomPanel.add(undoBtn);

        aiBtn = new JButton("🤖 Ativar IA");
        aiBtn.addActionListener(e -> {
            vsAI = !vsAI;
            aiBtn.setText(vsAI ? "🤖 IA Ativada" : "🤖 Ativar IA");
        });
        bottomPanel.add(aiBtn);

        add(bottomPanel, BorderLayout.SOUTH);

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

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                refresh();
            }
        });
        setVisible(true);
    }

    private void handleClick(int row, int col) {
        Piece p = board.at(row, col);

        if (selected == null) {
            if (p == null)
                return;

            if (p.color != board.sideToMove) {
                JOptionPane.showMessageDialog(this,
                        "Não, é a vez das " +
                                (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Pretas") + "!");
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

                turnLabel.setText("Vez das " + (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Pretas"));
                turnLabel.setBackground(
                        board.sideToMove == ChessColor.WHITE ? new Color(200, 230, 255) : new Color(255, 200, 200));

                if (board.inCheck(board.sideToMove)) {
                    if (board.isCheckmate(board.sideToMove)) {
                        JOptionPane.showMessageDialog(this,
                                "Cheque-mate! " +
                                        (board.sideToMove == ChessColor.WHITE ? "Pretas" : "Brancas") +
                                        " venceram!");

                        board = new Board();
                        refresh();
                        turnLabel.setText("Vez das Brancas");
                        turnLabel.setBackground(Color.LIGHT_GRAY);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Cheque nas " +
                                        (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Pretas") + "!");
                    }
                }

                if (board.inCheck(board.sideToMove)) {
                    JOptionPane.showMessageDialog(this,
                            "Você está em cheque! Só pode mover peças que tirem o rei do cheque.");
                }
            }
        }

        if (vsAI && board.sideToMove == ChessColor.BLACK) {
            makeAIMove();
        }
    }

    private void makeAIMove() {
        java.util.List<Move> moves = board.getAllLegalMoves(board.sideToMove);
        if (moves.isEmpty())
            return;

        Move move = moves.get(new java.util.Random().nextInt(moves.size()));

        Piece p = board.at(move.from.row, move.from.col);
        board.move(move.from, move.to);

        animateMove(move.from, move.to, p);
        refresh();

        turnLabel.setText("Vez das " + (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Pretas"));
        turnLabel.setBackground(
                board.sideToMove == ChessColor.WHITE ? new Color(200, 230, 255) : new Color(255, 200, 200));
    }

    private void resetColors() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                buttons[r][c].setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
            }
        }
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

        String name = "";
        if (p instanceof King)
            name = "king";
        if (p instanceof Queen)
            name = "queen";
        if (p instanceof Rook)
            name = "rook";
        if (p instanceof Bishop)
            name = "bishop";
        if (p instanceof Knight)
            name = "knight";
        if (p instanceof Pawn)
            name = "pawn";

        String path = "images/" + (p.color == ChessColor.WHITE ? "white_" : "black_") + name + ".png";
        ImageIcon icon = new ImageIcon(path);

        int w = btn.getWidth();
        int h = btn.getHeight();

        if (w <= 0 || h <= 0)
            return icon;

        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void refresh() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                buttons[r][c].setIcon(getPieceIcon(p, buttons[r][c]));
            }
        }

        for (ChessColor color : ChessColor.values()) {
            if (board.inCheck(color)) {
                Position kingPos = board.findKing(color);
                if (kingPos != null) {
                    buttons[kingPos.row][kingPos.col].setBackground(Color.RED);
                }
            }
        }
    }

}

import javax.swing.*;
import java.awt.*;

public class ChessUI extends JFrame {
    private JButton[][] buttons = new JButton[8][8];
    private Board board;
    private Position selected = null;
    private JLabel turnLabel;
    private final Color lightColor = new Color(240, 217, 181);
    private final Color darkColor = new Color(181, 136, 99);

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
                                (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Negras") + "!");
                return;
            }

            selected = new Position(row, col);
            buttons[row][col].setBackground(new Color(163, 102, 189));

            for (Position move : p.legalTargets(board, selected)) {
                buttons[move.row][move.col].setBackground(new Color(144, 238, 144));
            }
        } else {
            Position to = new Position(row, col);
            boolean moved = board.move(selected, to);

            resetColors();
            selected = null;

            if (moved) {
                refresh();

                turnLabel.setText("Vez das " + (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Negras"));
                turnLabel.setBackground(
                        board.sideToMove == ChessColor.WHITE ? new Color(200, 230, 255) : new Color(255, 200, 200));

                if (board.inCheck(board.sideToMove)) {
                    if (board.isCheckmate(board.sideToMove)) {
                        JOptionPane.showMessageDialog(this,
                                "Cheque-mate! " +
                                        (board.sideToMove == ChessColor.WHITE ? "Negras" : "Brancas") +
                                        " venceram!");

                        board = new Board();
                        refresh();
                        turnLabel.setText("Vez das Brancas");
                        turnLabel.setBackground(Color.LIGHT_GRAY);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Cheque nas " +
                                        (board.sideToMove == ChessColor.WHITE ? "Brancas" : "Negras") + "!");
                    }
                }

                if (board.inCheck(board.sideToMove)) {
                    JOptionPane.showMessageDialog(this,
                            "Você está em cheque! Só pode mover peças que tirem o rei do cheque.");
                }
            }
        }
    }

    private void resetColors() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                buttons[r][c].setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
            }
        }
    }

    private String getSymbol(Piece p) {
        if (p == null)
            return "";
        if (p instanceof King)
            return p.color == ChessColor.WHITE ? "\u2654" : "\u265A";
        if (p instanceof Queen)
            return p.color == ChessColor.WHITE ? "\u2655" : "\u265B";
        if (p instanceof Rook)
            return p.color == ChessColor.WHITE ? "\u2656" : "\u265C";
        if (p instanceof Bishop)
            return p.color == ChessColor.WHITE ? "\u2657" : "\u265D";
        if (p instanceof Knight)
            return p.color == ChessColor.WHITE ? "\u2658" : "\u265E";
        if (p instanceof Pawn)
            return p.color == ChessColor.WHITE ? "\u2659" : "\u265F";
        return "";
    }

    private void refresh() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                buttons[r][c].setText(getSymbol(p));
            }
        }
    }
}

import javax.swing.*;
import java.awt.*;

public class ChessUI extends JFrame {
    private final Board board = new Board();
    private final JButton[][] cells = new JButton[8][8];
    private Position selected = null;

    public ChessUI() {
        setTitle("Xadrez");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton b = new JButton();
                b.setFont(new Font("SansSerif", Font.PLAIN, 40));
                final int rr = r, cc = c;
                b.addActionListener(e -> onCellClick(rr, cc));
                cells[r][c] = b;
                add(b);
            }
        }
        refreshBoard();
    }

    private void onCellClick(int r, int c) {
        if (selected == null) {
            Piece p = board.at(r, c);
            if (p != null && p.color == board.sideToMove)
                selected = new Position(r, c);
        } else {
            if (!board.move(selected, new Position(r, c)))
                JOptionPane.showMessageDialog(this, "Movimento inválido");
            selected = null;
        }
        refreshBoard();
    }

    private void refreshBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                JButton b = cells[r][c];
                b.setText(getSymbol(p));
                b.setBackground((r + c) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
            }
        }
    }

private String getSymbol(Piece p){
    if(p==null) return "";
    if(p instanceof King) return p.color==ChessColor.WHITE?"\u2654":"\u265A";
    if(p instanceof Queen) return p.color==ChessColor.WHITE?"\u2655":"\u265B";
    if(p instanceof Rook) return p.color==ChessColor.WHITE?"\u2656":"\u265C";
    if(p instanceof Bishop) return p.color==ChessColor.WHITE?"\u2657":"\u265D";
    if(p instanceof Knight) return p.color==ChessColor.WHITE?"\u2658":"\u265E";
    if(p instanceof Pawn) return p.color==ChessColor.WHITE?"\u2659":"\u265F";
    return "";
}

}

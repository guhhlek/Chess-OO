import javax.swing.JOptionPane;

public class Board {
    public Piece[][] board;
    public ChessColor sideToMove;

    public Board() {
        board = new Piece[8][8];
        setup();
        sideToMove = ChessColor.WHITE;
    }

    private Board(boolean empty) {
        board = new Piece[8][8];
    }

    public void setup() {
        board[0][0] = new Rook(ChessColor.BLACK);
        board[0][7] = new Rook(ChessColor.BLACK);
        board[7][0] = new Rook(ChessColor.WHITE);
        board[7][7] = new Rook(ChessColor.WHITE);

        board[0][1] = new Knight(ChessColor.BLACK);
        board[0][6] = new Knight(ChessColor.BLACK);
        board[7][1] = new Knight(ChessColor.WHITE);
        board[7][6] = new Knight(ChessColor.WHITE);

        board[0][2] = new Bishop(ChessColor.BLACK);
        board[0][5] = new Bishop(ChessColor.BLACK);
        board[7][2] = new Bishop(ChessColor.WHITE);
        board[7][5] = new Bishop(ChessColor.WHITE);

        board[0][3] = new Queen(ChessColor.BLACK);
        board[7][3] = new Queen(ChessColor.WHITE);

        board[0][4] = new King(ChessColor.BLACK);
        board[7][4] = new King(ChessColor.WHITE);

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(ChessColor.BLACK);
            board[6][i] = new Pawn(ChessColor.WHITE);
        }
    }

    public Piece at(int r, int c) {
        return board[r][c];
    }

    public boolean move(Position from, Position to) {
        Piece p = board[from.row][from.col];
        if (p == null || p.color != sideToMove)
            return false;

        java.util.List<Position> moves = p.legalTargets(this, from);
        if (!moves.contains(to))
            return false;

        Piece captured = board[to.row][to.col];

        board[to.row][to.col] = p;
        board[from.row][from.col] = null;

        if (inCheck(p.color)) {
            board[from.row][from.col] = p;
            board[to.row][to.col] = captured;
            return false;
        }

        promotePawn(to);

        sideToMove = (sideToMove == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE);
        return true;
    }

    public Board copy() {
        Board b = new Board(true);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = this.at(r, c);
                if (p != null)
                    b.board[r][c] = p.clone();
            }
        }
        b.sideToMove = this.sideToMove;
        return b;
    }

    public boolean inCheck(ChessColor color) {
        Position kingPos = findKing(color);
        if (kingPos == null)
            return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece enemy = board[r][c];
                if (enemy != null && enemy.color != color) {
                    if (enemy.legalTargets(this, new Position(r, c)).contains(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(ChessColor color) {
        if (!inCheck(color))
            return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = at(r, c);
                if (p != null && p.color == color) {
                    for (Position move : p.legalTargets(this, new Position(r, c))) {
                        Board copy = this.copy();
                        if (copy.move(new Position(r, c), move) && !copy.inCheck(color)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void promotePawn(Position pos) {
        Piece p = at(pos.row, pos.col);
        if (!(p instanceof Pawn))
            return;

        if ((p.color == ChessColor.WHITE && pos.row == 0) ||
                (p.color == ChessColor.BLACK && pos.row == 7)) {

            String[] options = { "Queen", "Rook", "Bishop", "Knight" };
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Escolha a peça para promoção:",
                    "Promoção de Peão",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (choice) {
                case 0 -> board[pos.row][pos.col] = new Queen(p.color);
                case 1 -> board[pos.row][pos.col] = new Rook(p.color);
                case 2 -> board[pos.row][pos.col] = new Bishop(p.color);
                case 3 -> board[pos.row][pos.col] = new Knight(p.color);
                default -> board[pos.row][pos.col] = new Queen(p.color);
            }
        }
    }

    private Position findKing(ChessColor color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.color == color)
                    return new Position(r, c);
            }
        }
        return null;
    }
}

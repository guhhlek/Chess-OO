import java.util.*;

public class Board {
    private final Piece[][] board = new Piece[8][8];
    public ChessColor sideToMove = ChessColor.WHITE;

    public Board() {
        setupBoard();
    }

    public Piece at(int row, int col) {
        return board[row][col];
    }

    private void setupBoard() {
        // Torres
        board[0][0] = new Rook(ChessColor.BLACK);
        board[0][7] = new Rook(ChessColor.BLACK);
        board[7][0] = new Rook(ChessColor.WHITE);
        board[7][7] = new Rook(ChessColor.WHITE);

        // Cavalos
        board[0][1] = new Knight(ChessColor.BLACK);
        board[0][6] = new Knight(ChessColor.BLACK);
        board[7][1] = new Knight(ChessColor.WHITE);
        board[7][6] = new Knight(ChessColor.WHITE);

        // Bispos
        board[0][2] = new Bishop(ChessColor.BLACK);
        board[0][5] = new Bishop(ChessColor.BLACK);
        board[7][2] = new Bishop(ChessColor.WHITE);
        board[7][5] = new Bishop(ChessColor.WHITE);

        // Rainhas
        board[0][3] = new Queen(ChessColor.BLACK);
        board[7][3] = new Queen(ChessColor.WHITE);

        // Reis
        board[0][4] = new King(ChessColor.BLACK);
        board[7][4] = new King(ChessColor.WHITE);

        // Peões
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(ChessColor.BLACK);
            board[6][i] = new Pawn(ChessColor.WHITE);
        }
    }

    public boolean move(Position from, Position to) {
        Piece p = at(from.row, from.col);
        if (p == null || p.color != sideToMove)
            return false;

        List<Position> legal = p.legalTargets(this, from);
        if (!legal.contains(to))
            return false;

        board[to.row][to.col] = p;
        board[from.row][from.col] = null;
        sideToMove = sideToMove.opposite();
        return true;
    }
}

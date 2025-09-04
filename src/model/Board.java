package model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Board {
    private Piece[][] board;
    private Stack<Move> history;
    private ChessColor sideToMove;

    public Board() {
        board = new Piece[8][8];
        history = new Stack<>();
        sideToMove = ChessColor.WHITE;
        setup();
    }

    private Board(boolean empty) {
        board = new Piece[8][8];
        history = new Stack<>();
    }

    public ChessColor getSideToMove() {
        return sideToMove;
    }

    public Piece at(int r, int c) {
        if (!isInsideBoard(r, c))
            return null;
        return board[r][c];
    }

    public boolean isInsideBoard(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    public boolean move(Position from, Position to) {
        Piece p = at(from.row, from.col);
        if (p == null || p.getColor() != sideToMove)
            return false;

        List<Position> legal = p.legalTargets(this, from);
        if (!legal.contains(to))
            return false;

        Piece captured = at(to.row, to.col);
        history.push(new Move(from, to, p, captured));

        board[to.row][to.col] = p;
        board[from.row][from.col] = null;

        if (inCheck(p.getColor())) {
            board[from.row][from.col] = p;
            board[to.row][to.col] = captured;
            history.pop();
            return false;
        }

        promotePawn(to);

        sideToMove = (sideToMove == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
        return true;
    }

    public boolean undo() {
        if (history.isEmpty())
            return false;

        Move last = history.pop();
        board[last.from.row][last.from.col] = last.moved;
        board[last.to.row][last.to.col] = last.captured;
        sideToMove = (sideToMove == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
        return true;
    }

    public Board copy() {
        Board b = new Board(true);
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != null)
                    b.board[r][c] = board[r][c].clone();

        b.sideToMove = sideToMove;
        return b;
    }

    public boolean inCheck(ChessColor color) {
        Position kingPos = findKing(color);
        if (kingPos == null)
            return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece enemy = at(r, c);
                if (enemy != null && enemy.getColor() != color) {
                    if (enemy.legalTargets(this, new Position(r, c)).contains(kingPos))
                        return true;
                }
            }
        }
        return false;
    }

    public Position findKing(ChessColor color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.getColor() == color)
                    return new Position(r, c);
            }
        return null;
    }

    public List<Move> getAllLegalMoves(ChessColor color) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = at(r, c);
                if (p != null && p.getColor() == color) {
                    for (Position to : p.legalTargets(this, new Position(r, c))) {
                        Board copy = copy();
                        if (copy.move(new Position(r, c), to) && !copy.inCheck(color)) {
                            moves.add(new Move(new Position(r, c), to, p, at(to.row, to.col)));
                        }
                    }
                }
            }
        }
        return moves;
    }

    public boolean isCheckmate(ChessColor color) {
        if (!inCheck(color))
            return false;
        return getAllLegalMoves(color).isEmpty();
    }

    private void promotePawn(Position pos) {
        Piece p = at(pos.row, pos.col);
        if (!(p instanceof Pawn))
            return;

        if ((p.getColor() == ChessColor.WHITE && pos.row == 0) ||
                (p.getColor() == ChessColor.BLACK && pos.row == 7)) {
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

            board[pos.row][pos.col] = switch (choice) {
                case 0 -> new Queen(p.getColor());
                case 1 -> new Rook(p.getColor());
                case 2 -> new Bishop(p.getColor());
                case 3 -> new Knight(p.getColor());
                default -> new Queen(p.getColor());
            };
        }
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
}

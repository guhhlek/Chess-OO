package model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(ChessColor color) {
        super(color);
    }

    @Override
    public List<Position> legalTargets(Board board, Position pos) {
        List<Position> moves = new ArrayList<>();
        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };

        for (int i = 0; i < dx.length; i++) {
            int newRow = pos.row + dx[i];
            int newCol = pos.col + dy[i];
            if (board.isInsideBoard(newRow, newCol)) {
                Piece p = board.at(newRow, newCol);
                if (p == null || p.color != this.color)
                    moves.add(new Position(newRow, newCol));
            }
        }
        return moves;
    }
}

package model;
import java.util.*;

public class Knight extends Piece {
    public Knight(ChessColor color) {
        super(color);
    }

    @Override
    public List<Position> legalTargets(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[] dr = { -2, -2, -1, -1, 1, 1, 2, 2 };
        int[] dc = { -1, 1, -2, 2, -2, 2, -1, 1 };

        for (int i = 0; i < 8; i++) {
            int r = from.row + dr[i];
            int c = from.col + dc[i];
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board.at(r, c);
                if (p == null || p.color != this.color)
                    moves.add(new Position(r, c));
            }
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new Knight(this.color);
    }
}

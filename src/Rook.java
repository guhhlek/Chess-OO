import java.util.*;

public class Rook extends Piece {
    public Rook(ChessColor color) {
        super(color);
    }

    @Override
    public List<Position> legalTargets(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        for (int[] d : dirs) {
            int r = from.row + d[0];
            int c = from.col + d[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board.at(r, c);
                if (p == null)
                    moves.add(new Position(r, c));
                else {
                    if (p.color != this.color)
                        moves.add(new Position(r, c));
                    break;
                }
                r += d[0];
                c += d[1];
            }
        }
        return moves;
    }
}

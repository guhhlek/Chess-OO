import java.util.*;

public class Pawn extends Piece {
    public Pawn(ChessColor color) {
        super(color);
    }

    @Override
    public List<Position> legalTargets(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int dir = color == ChessColor.WHITE ? -1 : 1;
        int startRow = color == ChessColor.WHITE ? 6 : 1;

        // mover 1 casa
        int r = from.row + dir;
        if (r >= 0 && r < 8 && board.at(r, from.col) == null) {
            moves.add(new Position(r, from.col));

            // mover 2 casas do início
            if (from.row == startRow && board.at(r + dir, from.col) == null) {
                moves.add(new Position(r + dir, from.col));
            }
        }

        // capturas
        if (from.col > 0) {
            Piece p = board.at(r, from.col - 1);
            if (p != null && p.color != color)
                moves.add(new Position(r, from.col - 1));
        }
        if (from.col < 7) {
            Piece p = board.at(r, from.col + 1);
            if (p != null && p.color != color)
                moves.add(new Position(r, from.col + 1));
        }

        return moves;
    }
}

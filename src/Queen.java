import java.util.*;

public class Queen extends Piece {
    public Queen(ChessColor color) {
        super(color);
    }

    @Override
    public List<Position> legalTargets(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(new Rook(color).legalTargets(board, from));
        moves.addAll(new Bishop(color).legalTargets(board, from));
        return moves;
    }

    @Override
    public Piece clone() {
        return new Queen(this.color);
    }
}

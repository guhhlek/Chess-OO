package ai;

import model.*;
import java.util.List;
import java.util.Random;

public class MediumAI extends ChessAI {
    private Random random = new Random();

    public MediumAI(ChessColor color) {
        super(color);
    }

    @Override
    public Move chooseMove(Board board) {
        List<Move> moves = board.getAllLegalMoves(color);
        if (moves.isEmpty())
            return null;

        for (Move m : moves) {
            if (board.at(m.to.row, m.to.col) != null)
                return m;
        }

        return moves.get(random.nextInt(moves.size()));
    }
}

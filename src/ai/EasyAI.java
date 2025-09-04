package ai;

import model.*;
import java.util.List;
import java.util.Random;

public class EasyAI extends ChessAI {
    private Random random = new Random();

    public EasyAI(ChessColor color) {
        super(color);
    }

    @Override
    public Move chooseMove(Board board) {
        List<Move> moves = board.getAllLegalMoves(color);
        if (moves.isEmpty())
            return null;

        return moves.get(random.nextInt(moves.size()));
    }
}

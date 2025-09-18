package ai;

import model.*;
import java.util.List;

public class HardAI extends ChessAI {
    private int depth = 3; // 2 (menos dificil) ou 3 para mais dificuldade

    public HardAI(ChessColor color) {
        super(color);
    }

    @Override
    public Move chooseMove(Board board) {
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        List<Move> moves = board.getAllLegalMoves(color);
        for (Move move : moves) {
            Board copy = board.copy();
            copy.move(move.from, move.to);
            int value = minimax(copy, depth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(Board board, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0 || board.isCheckmate(color) || board.isCheckmate(opponent(color))) {
            return evaluateBoard(board);
        }

        List<Move> moves = board.getAllLegalMoves(maximizingPlayer ? color : opponent(color));

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                copy.move(move.from, move.to);
                int eval = minimax(copy, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                copy.move(move.from, move.to);
                int eval = minimax(copy, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    private int evaluateBoard(Board board) {
        int score = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if (p != null) {
                    int value = switch (p.getClass().getSimpleName()) {
                        case "Pawn" -> 10;
                        case "Knight", "Bishop" -> 30;
                        case "Rook" -> 50;
                        case "Queen" -> 90;
                        case "King" -> 900;
                        default -> 0;
                    };
                    score += (p.getColor() == color ? value : -value);
                }
            }
        }
        return score;
    }

    private ChessColor opponent(ChessColor c) {
        return c == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
    }
}

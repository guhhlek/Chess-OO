package ai;

import model.*;

import java.util.List;

public class HardAI extends ChessAI {
    private final int MAX_DEPTH = 3; // Profundidade do Minimax

    public HardAI(ChessColor color) {
        super(color);
    }

    @Override
    public Move chooseMove(Board board) {
        return minimaxRoot(board, MAX_DEPTH, color);
    }

    private Move minimaxRoot(Board board, int depth, ChessColor color) {
        List<Move> moves = board.getAllLegalMoves(color);
        if (moves.isEmpty())
            return null;

        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Move move : moves) {
            Board copy = board.copy();
            copy.move(move.from, move.to);

            int value = minimax(copy, depth - 1, false, color, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimax(Board board, int depth, boolean maximizingPlayer, ChessColor myColor, int alpha, int beta) {
        if (depth == 0)
            return evaluateBoard(board, myColor);

        ChessColor currentColor = maximizingPlayer ? myColor
                : (myColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE);
        List<Move> moves = board.getAllLegalMoves(currentColor);

        if (moves.isEmpty()) {
            if (board.inCheck(currentColor)) {
                return maximizingPlayer ? Integer.MIN_VALUE + 1 : Integer.MAX_VALUE - 1;
            } else {
                return 0;
            }
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board copy = board.copy();
                copy.move(move.from, move.to);
                int eval = minimax(copy, depth - 1, false, myColor, alpha, beta);
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
                int eval = minimax(copy, depth - 1, true, myColor, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    private int evaluateBoard(Board board, ChessColor myColor) {
        int score = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if (p == null)
                    continue;

                int value = getPieceValue(p);
                if (p.getColor() == myColor)
                    score += value;
                else
                    score -= value;

                if (c >= 2 && c <= 5 && r >= 2 && r <= 5) {
                    score += (p.getColor() == myColor ? 5 : -5);
                }

                int mobility = p.legalTargets(board, new Position(r, c)).size();
                score += (p.getColor() == myColor ? mobility : -mobility);
            }
        }

        Position myKing = board.findKing(myColor);
        if (myKing != null) {
            score -= countAttacks(board, myKing, myColor) * 10;
        }

        Position enemyKing = board.findKing(myColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE);
        if (enemyKing != null) {
            score += countAttacks(board, enemyKing, myColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE)
                    * 10;
        }

        return score;
    }

    private int countAttacks(Board board, Position pos, ChessColor color) {
        int attacks = 0;
        ChessColor enemy = (color == ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.at(r, c);
                if (p != null && p.getColor() == enemy) {
                    List<Position> targets = p.legalTargets(board, new Position(r, c));
                    if (targets.contains(pos))
                        attacks++;
                }
            }
        }
        return attacks;
    }

    private int getPieceValue(Piece p) {
        return switch (p.getClass().getSimpleName()) {
            case "Pawn" -> 10;
            case "Knight" -> 30;
            case "Bishop" -> 30;
            case "Rook" -> 50;
            case "Queen" -> 90;
            case "King" -> 900;
            default -> 0;
        };
    }
}

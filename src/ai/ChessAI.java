package ai;

import model.*;

public abstract class ChessAI {
    protected ChessColor color;

    public ChessAI(ChessColor color) {
        this.color = color;
    }

    public ChessColor getColor() {
        return color;
    }

    public abstract Move chooseMove(Board board);
}

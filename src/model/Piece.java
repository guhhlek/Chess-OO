package model;

import java.util.List;

public abstract class Piece implements Cloneable {
    protected ChessColor color;

    public Piece(ChessColor color) {
        this.color = color;
    }

    public ChessColor getColor() {
        return color;
    }

    public abstract List<Position> legalTargets(Board board, Position pos);

    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

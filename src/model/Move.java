package model;

public class Move {
    public Position from;
    public Position to;
    public Piece moved;
    public Piece captured;

    public Move(Position from, Position to, Piece moved, Piece captured) {
        this.from = from;
        this.to = to;
        this.moved = moved;
        this.captured = captured;
    }
}

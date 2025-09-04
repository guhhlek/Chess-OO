public class Move {
    Position from, to;
    Piece moved, captured;

    Move(Position from, Position to, Piece moved, Piece captured) {
        this.from = from;
        this.to = to;
        this.moved = moved;
        this.captured = captured;
    }
}

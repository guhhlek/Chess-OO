import java.util.List;

public abstract class Piece {
    public final ChessColor color;

    public Piece(ChessColor color) {
        this.color = color;
    }

    // Todas as peças devem implementar isso para movimentos legais
    public abstract List<Position> legalTargets(Board board, Position from);

    // Adicione este método abstrato
    public abstract Piece clone();
}

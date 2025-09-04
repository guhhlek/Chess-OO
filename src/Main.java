import javax.swing.SwingUtilities;

import model.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            board.setup();
            new ChessUI(board);
        });
    }
}

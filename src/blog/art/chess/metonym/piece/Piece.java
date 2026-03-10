/*
 * MIT License
 *
 * Copyright (c) 2026 Ivan Denkovski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package blog.art.chess.metonym.piece;

import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.position.Direction;
import blog.art.chess.metonym.position.Operation;
import blog.art.chess.metonym.position.Section;
import blog.art.chess.metonym.position.Square;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

public abstract class Piece {

  protected final boolean black;

  protected Piece(boolean black) {
    this.black = black;
  }

  public boolean isBlack() {
    return black;
  }

  public abstract boolean generateMoves(Square origin, Map<Square, Piece> board,
      Map<Section, Piece> box, Set<Square> castlingOrigins, Square enPassantTarget,
      List<Move> moves);

  public abstract String getLanCode();

  protected static final Map<Set<Direction>, List<Direction>> DIRECTIONS = new HashMap<>();

  protected static List<Direction> computeDirections(Set<Direction> bases) {
    Set<Direction> directions = new TreeSet<>(Comparator.comparingInt(Direction::getFileOffset)
        .thenComparingInt(Direction::getRankOffset));
    for (Direction base : bases) {
      for (int fileOffset : new int[]{-base.getFileOffset(), base.getFileOffset()}) {
        for (int rankOffset : new int[]{-base.getRankOffset(), base.getRankOffset()}) {
          directions.add(new Direction(fileOffset, rankOffset));
          directions.add(new Direction(rankOffset, fileOffset));
        }
      }
    }
    return new ArrayList<>(directions);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Piece.class.getSimpleName() + "[", "]").add("black=" + black)
        .toString();
  }

  public static void validate(Map<Square, Piece> board, boolean blackToMove,
      Set<Square> castlingOrigins, Square enPassantTarget) {
    for (boolean black : new boolean[]{false, true}) {
      int frequency = 0;
      for (Piece piece : board.values()) {
        if (piece instanceof King && piece.black == black) {
          frequency++;
        }
      }
      if (!(frequency == 1)) {
        throw new IllegalArgumentException("Not accepted number of kings");
      }
    }
    for (Square castlingOrigin : castlingOrigins) {
      Piece piece = board.get(castlingOrigin);
      if (!((castlingOrigin.getFile() == 5 && piece instanceof King
          || (castlingOrigin.getFile() == 1 || castlingOrigin.getFile() == 8)
          && piece instanceof Rook) && (castlingOrigin.getRank() == 1 && !piece.black
          || castlingOrigin.getRank() == 8 && piece.black))) {
        throw new IllegalArgumentException("Not accepted castling rights");
      }
    }
    if (enPassantTarget != null) {
      Piece captured = board.get(new Square(enPassantTarget.getFile(), blackToMove ? 4 : 5));
      if (!(enPassantTarget.getRank() == (blackToMove ? 3 : 6) && captured instanceof Pawn
          && captured.black != blackToMove && board.get(enPassantTarget) == null
          && board.get(new Square(enPassantTarget.getFile(), blackToMove ? 2 : 7)) == null)) {
        throw new IllegalArgumentException("Not accepted en passant square");
      }
    }
  }

  public static String formatToString(Map<Square, Piece> board, boolean blackToMove,
      Set<Square> castlingOrigins, Square enPassantTarget, Operation operation) {
    List<String> args = new ArrayList<>();
    for (int rank = 8; rank >= 1; rank--) {
      for (int file = 1; file <= 8; file++) {
        Piece piece = board.get(new Square(file, rank));
        args.add(piece instanceof King ? (piece.black ? "k" : "K")
            : piece instanceof Queen ? (piece.black ? "q" : "Q")
                : piece instanceof Rook ? (piece.black ? "r" : "R")
                    : piece instanceof Bishop ? (piece.black ? "b" : "B")
                        : piece instanceof Knight ? (piece.black ? "n" : "N")
                            : piece instanceof Pawn ? (piece.black ? "p" : "P") : ".");
      }
      if (rank == 8) {
        args.add(blackToMove ? "b" : "w");
      } else if (rank == 7) {
        if (!castlingOrigins.isEmpty()) {
          StringBuilder arg = new StringBuilder();
          if (castlingOrigins.contains(new Square(5, 1))) {
            if (castlingOrigins.contains(new Square(8, 1))) {
              arg.append("K");
            }
            if (castlingOrigins.contains(new Square(1, 1))) {
              arg.append("Q");
            }
          }
          if (castlingOrigins.contains(new Square(5, 8))) {
            if (castlingOrigins.contains(new Square(8, 8))) {
              arg.append("k");
            }
            if (castlingOrigins.contains(new Square(1, 8))) {
              arg.append("q");
            }
          }
          args.add(arg.toString());
        } else {
          args.add("-");
        }
      } else if (rank == 6) {
        if (enPassantTarget != null) {
          args.add("" + (char) ('a' + enPassantTarget.getFile() - 1) + (char) (
              '1' + enPassantTarget.getRank() - 1));
        } else {
          args.add("-");
        }
      } else if (rank == 4) {
        args.add(operation.getSummary());
      }
    }
    return String.format(("8 %s %s %s %s %s %s %s %s    Side to move: %s%n"
            + "7 %s %s %s %s %s %s %s %s    Castling rights: %s%n"
            + "6 %s %s %s %s %s %s %s %s    En passant target: %s%n" + "5 %s %s %s %s %s %s %s %s%n"
            + "4 %s %s %s %s %s %s %s %s    %s%n" + "3 %s %s %s %s %s %s %s %s%n"
            + "2 %s %s %s %s %s %s %s %s%n" + "1 %s %s %s %s %s %s %s %s%n" + "  a b c d e f g h"),
        args.toArray());
  }
}

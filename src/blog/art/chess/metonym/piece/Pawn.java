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

import blog.art.chess.metonym.move.Capture;
import blog.art.chess.metonym.move.DoubleStep;
import blog.art.chess.metonym.move.EnPassant;
import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.move.Promotion;
import blog.art.chess.metonym.move.PromotionCapture;
import blog.art.chess.metonym.move.QuietMove;
import blog.art.chess.metonym.position.Direction;
import blog.art.chess.metonym.position.Section;
import blog.art.chess.metonym.position.Square;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public final class Pawn extends Piece {

  public Pawn(boolean black) {
    super(black);
  }

  @Override
  public boolean generateMoves(Square origin, Map<Square, Piece> board, Map<Section, Piece> box,
      Set<Square> castlingOrigins, Square enPassantTarget, List<Move> moves) {
    List<Direction> captureDirections = Arrays.asList(new Direction(-1, black ? -1 : 1),
        new Direction(1, black ? -1 : 1));
    for (Direction direction : captureDirections) {
      Square target = new Square(origin.getFile() + direction.getFileOffset(),
          origin.getRank() + direction.getRankOffset());
      if (target.getFile() >= 1 && target.getFile() <= 8 && target.getRank() >= 1
          && target.getRank() <= 8) {
        Piece other = board.get(target);
        if (other != null) {
          if (other.black != black) {
            if (other instanceof King) {
              return false;
            } else {
              if (origin.getRank() == (black ? 2 : 7)) {
                for (Section section : box.keySet()) {
                  if (section.isBlack() == black) {
                    if (moves != null) {
                      moves.add(new PromotionCapture(origin, target, section));
                    }
                  }
                }
              } else {
                if (moves != null) {
                  moves.add(new Capture(origin, target));
                }
              }
            }
          }
        } else {
          if (enPassantTarget != null) {
            if (target.equals(enPassantTarget)) {
              Square stop = new Square(target.getFile(), origin.getRank());
              if (moves != null) {
                moves.add(new EnPassant(origin, target, stop));
              }
            }
          }
        }
      }
    }
    Direction direction = new Direction(0, black ? -1 : 1);
    Square target = new Square(origin.getFile() + direction.getFileOffset(),
        origin.getRank() + direction.getRankOffset());
    if (target.getFile() >= 1 && target.getFile() <= 8 && target.getRank() >= 1
        && target.getRank() <= 8) {
      if (board.get(target) == null) {
        if (origin.getRank() == (black ? 2 : 7)) {
          for (Section section : box.keySet()) {
            if (section.isBlack() == black) {
              if (moves != null) {
                moves.add(new Promotion(origin, target, section));
              }
            }
          }
        } else {
          if (moves != null) {
            moves.add(new QuietMove(origin, target));
          }
          if (origin.getRank() == (black ? 7 : 2)) {
            Square target2 = new Square(origin.getFile() + 2 * direction.getFileOffset(),
                origin.getRank() + 2 * direction.getRankOffset());
            if (board.get(target2) == null) {
              if (moves != null) {
                moves.add(new DoubleStep(origin, target2, target));
              }
            }
          }
        }
      }
    }
    return true;
  }

  @Override
  public String getLanCode() {
    return "";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Pawn.class.getSimpleName() + "[", "]").add("black=" + black)
        .toString();
  }
}

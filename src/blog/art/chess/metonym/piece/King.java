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

import blog.art.chess.metonym.move.LongCastling;
import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.move.ShortCastling;
import blog.art.chess.metonym.position.Direction;
import blog.art.chess.metonym.position.Section;
import blog.art.chess.metonym.position.Square;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public final class King extends Leaper {

  public King(boolean black) {
    super(black);
  }

  @Override
  protected Set<Direction> getLeaps() {
    return new HashSet<>(Arrays.asList(new Direction(0, 1), new Direction(1, 1)));
  }

  @Override
  public boolean generateMoves(Square origin, Map<Square, Piece> board, Map<Section, Piece> box,
      Set<Square> castlingOrigins, Square enPassantTarget, List<Move> moves) {
    if (!super.generateMoves(origin, board, box, castlingOrigins, enPassantTarget, moves)) {
      return false;
    }
    if (castlingOrigins.contains(origin)) {
      List<Direction> castlingDirections = Arrays.asList(new Direction(-1, 0), new Direction(1, 0));
      for (Direction direction : castlingDirections) {
        int distance = 1;
        Square target2 = new Square(origin.getFile() + distance * direction.getFileOffset(),
            origin.getRank() + distance * direction.getRankOffset());
        if (board.get(target2) == null) {
          distance++;
          Square target = new Square(origin.getFile() + distance * direction.getFileOffset(),
              origin.getRank() + distance * direction.getRankOffset());
          if (board.get(target) == null) {
            distance++;
            if (direction.getFileOffset() > 0) {
              Square origin2 = new Square(origin.getFile() + distance * direction.getFileOffset(),
                  origin.getRank() + distance * direction.getRankOffset());
              if (castlingOrigins.contains(origin2)) {
                if (moves != null) {
                  moves.add(new ShortCastling(origin, target, origin2, target2));
                }
              }
            } else {
              Square stop = new Square(origin.getFile() + distance * direction.getFileOffset(),
                  origin.getRank() + distance * direction.getRankOffset());
              if (board.get(stop) == null) {
                distance++;
                Square origin2 = new Square(origin.getFile() + distance * direction.getFileOffset(),
                    origin.getRank() + distance * direction.getRankOffset());
                if (castlingOrigins.contains(origin2)) {
                  if (moves != null) {
                    moves.add(new LongCastling(origin, target, origin2, target2));
                  }
                }
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
    return "K";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", King.class.getSimpleName() + "[", "]").add("black=" + black)
        .toString();
  }
}

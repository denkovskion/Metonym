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

package blog.art.chess.metonym.move;

import blog.art.chess.metonym.position.Position;
import blog.art.chess.metonym.position.Square;
import java.util.StringJoiner;

public final class ShortCastling implements Move {

  private final Square origin;
  private final Square target;
  private final Square origin2;
  private final Square target2;

  public ShortCastling(Square origin, Square target, Square origin2, Square target2) {
    this.origin = origin;
    this.target = target;
    this.origin2 = origin2;
    this.target2 = target2;
  }

  @Override
  public boolean doMake(Position position, StringBuilder lanBuilder) {
    if (lanBuilder != null) {
      lanBuilder.append("0-0");
    }
    Move nullMove = new NullMove();
    boolean result = position.makeMove(nullMove, null, null);
    position.unmakeMove();
    if (result) {
      Move quietMove = new QuietMove(origin, target2);
      result = position.makeMove(quietMove, null, null);
      position.unmakeMove();
    }
    position.getBoard().put(target, position.getBoard().remove(origin));
    position.getBoard().put(target2, position.getBoard().remove(origin2));
    position.getCastlingOrigins().remove(origin);
    position.getCastlingOrigins().remove(origin2);
    position.setEnPassantTarget(null);
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ShortCastling.class.getSimpleName() + "[", "]").add(
            "origin=" + origin).add("target=" + target).add("origin2=" + origin2)
        .add("target2=" + target2).toString();
  }
}

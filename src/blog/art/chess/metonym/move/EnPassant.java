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

public final class EnPassant implements Move {

  private final Square origin;
  private final Square target;
  private final Square stop;

  public EnPassant(Square origin, Square target, Square stop) {
    this.origin = origin;
    this.target = target;
    this.stop = stop;
  }

  @Override
  public boolean doMake(Position position, StringBuilder lanBuilder) {
    if (lanBuilder != null) {
      lanBuilder.append(position.getBoard().get(origin).getLanCode()).append(origin.getLanCode())
          .append("x").append(target.getLanCode()).append(" e.p.");
    }
    position.getBoard().remove(stop);
    position.getBoard().put(target, position.getBoard().remove(origin));
    position.setEnPassantTarget(null);
    return true;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", EnPassant.class.getSimpleName() + "[", "]").add(
        "origin=" + origin).add("target=" + target).add("stop=" + stop).toString();
  }
}

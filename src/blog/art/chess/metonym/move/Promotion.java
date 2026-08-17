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
import blog.art.chess.metonym.position.Section;
import blog.art.chess.metonym.position.Square;
import java.util.StringJoiner;

public final class Promotion implements Move {

  private final Square origin;
  private final Square target;
  private final Section section;

  public Promotion(Square origin, Square target, Section section) {
    this.origin = origin;
    this.target = target;
    this.section = section;
  }

  @Override
  public void preWrite(Position position, StringBuilder lanBuilder) {
    lanBuilder.append(position.getBoard().get(origin).getLanCode()).append(origin.getLanCode())
        .append("-").append(target.getLanCode()).append("=")
        .append(position.getBox().get(section).getLanCode());
  }

  @Override
  public boolean preMake(Position position) {
    return true;
  }

  @Override
  public void updateState(Position position) {
    position.getBoard().remove(origin);
    position.getBoard().put(target, position.getBox().get(section));
    position.setEnPassantTarget(null);
    position.setBlackToMove(!position.isBlackToMove());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Promotion.class.getSimpleName() + "[", "]").add(
        "origin=" + origin).add("target=" + target).add("section=" + section).toString();
  }
}

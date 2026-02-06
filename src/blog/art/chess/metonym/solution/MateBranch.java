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

package blog.art.chess.metonym.solution;

import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.position.Position;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public final class MateBranch extends Node {

  private final Move move;
  private final int distance;
  private final List<Node> children;

  public MateBranch(Move move, int distance, List<Node> children) {
    this.move = move;
    this.distance = distance;
    this.children = children;
  }

  public int getDistance() {
    return distance;
  }

  @Override
  protected void doFormat(Position position, StringBuilder output, int moveNo, boolean inline) {
    if (position.isBlackToMove()) {
      if (!inline) {
        output.append(moveNo).append("...");
      }
    } else {
      output.append(moveNo).append(".");
    }
    position.makeMove(move, null, output);
    boolean first = true;
    for (Node child : children) {
      if (first) {
        output.append(" ");
      } else {
        output.append(System.lineSeparator()).append(String.join("",
            Collections.nCopies(position.isBlackToMove() ? moveNo - 1 : moveNo, "\t")));
      }
      child.doFormat(position, output, position.isBlackToMove() ? moveNo : moveNo + 1, first);
      first = false;
    }
    position.unmakeMove();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MateBranch.class.getSimpleName() + "[", "]").add("move=" + move)
        .add("distance=" + distance).add("children=" + children).toString();
  }
}

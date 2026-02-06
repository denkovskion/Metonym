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

package blog.art.chess.metonym.problem;

import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.position.Operation;
import blog.art.chess.metonym.position.Position;
import blog.art.chess.metonym.solution.IllegalNode;
import blog.art.chess.metonym.solution.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

public abstract class Problem implements Operation {

  private static final Logger LOGGER = Logger.getLogger(Problem.class.getName());

  protected final Position position;

  protected Problem(Position position) {
    this.position = position;
  }

  public void solve(boolean detailed, boolean verbose) {
    System.out.printf("%s%n%s%n%n", String.join("", Collections.nCopies(42, "_")),
        Position.formatToString(position, this));
    LOGGER.info("Solving...");
    long begin = System.currentTimeMillis();
    List<Move> pseudoLegalMoves = new ArrayList<>();
    Node solution;
    if (position.isLegal(pseudoLegalMoves)) {
      solution = doSolve(pseudoLegalMoves, detailed, verbose);
    } else {
      solution = new IllegalNode();
    }
    System.out.println(Node.formatToString(solution, position));
    long end = System.currentTimeMillis();
    LOGGER.info(String.format("Finished solving in %dms.", end - begin));
  }

  protected abstract Node doSolve(List<Move> pseudoLegalMoves, boolean detailed, boolean verbose);

  @Override
  public String toString() {
    return new StringJoiner(", ", Problem.class.getSimpleName() + "[", "]").add(
        "position=" + position).toString();
  }
}

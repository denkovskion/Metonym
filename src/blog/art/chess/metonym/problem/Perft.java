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
import blog.art.chess.metonym.position.Position;
import blog.art.chess.metonym.solution.CountNode;
import blog.art.chess.metonym.solution.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class Perft extends Problem {

  private static final Logger LOGGER = Logger.getLogger(Perft.class.getName());

  private final int nPlies;

  public Perft(Position position, int nPlies) {
    super(position);
    this.nPlies = nPlies;
  }

  @Override
  protected Node doSolve(List<Move> pseudoLegalMoves, boolean detailed, boolean verbose) {
    List<Node> nodes = detailed ? new ArrayList<>() : null;
    long nNodes = count(nPlies, position, pseudoLegalMoves, nodes, verbose);
    return new CountNode(null, nNodes, nodes);
  }

  private static long count(int nPlies, Position position, List<Move> pseudoLegalMoves,
      List<Node> nodes, boolean verbose) {
    if (nPlies == 0) {
      return 1;
    }
    long nNodes = 0;
    for (Move move : pseudoLegalMoves) {
      List<Move> pseudoLegalMovesNext = new ArrayList<>();
      StringBuilder lanBuilder = verbose ? new StringBuilder() : null;
      if (position.makeMove(move, pseudoLegalMovesNext, lanBuilder)) {
        long nChildNodes = count(nPlies - 1, position, pseudoLegalMovesNext, null, false);
        if (nodes != null) {
          nodes.add(new CountNode(move, nChildNodes, null));
        }
        nNodes += nChildNodes;
        if (verbose) {
          LOGGER.fine(String.format("Evaluated '%s'. Counted %d nodes at depth %d.", lanBuilder,
              nChildNodes, nPlies));
        }
      }
      position.unmakeMove();
    }
    if (verbose) {
      LOGGER.fine(String.format("Finished counting. %d nodes at depth %d.", nNodes, nPlies));
    }
    return nNodes;
  }

  @Override
  public String getSummary() {
    return String.format("Perft at depth %d", nPlies);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Perft.class.getSimpleName() + "[", "]").add("nPlies=" + nPlies)
        .add("position=" + position).toString();
  }
}

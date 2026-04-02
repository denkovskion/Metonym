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
import blog.art.chess.metonym.move.NullMove;
import blog.art.chess.metonym.position.Position;
import blog.art.chess.metonym.solution.MateBranch;
import blog.art.chess.metonym.solution.MateLeaf;
import blog.art.chess.metonym.solution.MateRoot;
import blog.art.chess.metonym.solution.Node;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class MateSearch extends Problem {

  private static final Logger LOGGER = Logger.getLogger(MateSearch.class.getName());

  private final int nMoves;

  public MateSearch(Position position, int nMoves) {
    super(position);
    this.nMoves = nMoves;
  }

  @Override
  protected Node doSolve(Position position, List<Move> pseudoLegalMoves, boolean detailed,
      boolean verbose) {
    List<Node> nodes = analyse(nMoves, position, pseudoLegalMoves, detailed, verbose);
    return new MateRoot(nodes);
  }

  private static List<Node> analyse(int nMoves, Position position, List<Move> pseudoLegalMoves,
      boolean detailed, boolean verbose) {
    List<Node> nodes = new ArrayList<>();
    for (Move moveMax : pseudoLegalMoves) {
      List<Move> pseudoLegalMovesMin = new ArrayList<>();
      StringBuilder lanBuilder = verbose ? new StringBuilder() : null;
      if (position.makeMove(moveMax, pseudoLegalMovesMin, lanBuilder)) {
        int min = searchMin(nMoves, position, pseudoLegalMovesMin);
        if (min > 0) {
          int distanceMax = nMoves - min + 1;
          if (verbose) {
            LOGGER.fine(String.format("Tried '%s'. Found mate in %d.", lanBuilder, distanceMax));
          }
          if (detailed) {
            List<Node> nodesMin = new ArrayList<>();
            for (Move moveMin : pseudoLegalMovesMin) {
              List<Move> pseudoLegalMovesMax = new ArrayList<>();
              if (position.makeMove(moveMin, pseudoLegalMovesMax, null)) {
                int max = searchMax(distanceMax - 1, position, pseudoLegalMovesMax);
                int distanceMin = distanceMax - max;
                List<Node> nodesMax = analyse(distanceMin, position, pseudoLegalMovesMax, true,
                    false);
                nodesMin.add(new MateBranch(moveMin, distanceMin, nodesMax));
              }
              position.unmakeMove();
            }
            nodesMin.sort(
                Comparator.comparingInt(node -> ((MateBranch) node).getDistance()).reversed());
            nodes.add(new MateBranch(moveMax, distanceMax, nodesMin));
            if (verbose) {
              LOGGER.fine(String.format("Finished analysis of '%s'.", lanBuilder));
            }
          } else {
            nodes.add(new MateLeaf(moveMax, distanceMax));
          }
        } else {
          if (verbose) {
            LOGGER.fine(String.format("Tried '%s'. No mate in %d.", lanBuilder, nMoves));
          }
        }
      }
      position.unmakeMove();
    }
    nodes.sort(Comparator.comparingInt(
        node -> detailed ? ((MateBranch) node).getDistance() : ((MateLeaf) node).getDistance()));
    return nodes;
  }

  private static int searchMax(int nMoves, Position position, List<Move> pseudoLegalMovesMax) {
    int max = -1;
    for (Move moveMax : pseudoLegalMovesMax) {
      List<Move> pseudoLegalMovesMin = new ArrayList<>();
      if (position.makeMove(moveMax, pseudoLegalMovesMin, null)) {
        int min = searchMin(nMoves, position, pseudoLegalMovesMin);
        if (min > max) {
          max = min;
        }
      }
      position.unmakeMove();
      if (max == nMoves) {
        break;
      }
    }
    return max;
  }

  private static int searchMin(int nMoves, Position position, List<Move> pseudoLegalMovesMin) {
    int min = 0;
    if (nMoves == 1) {
      for (Move moveMin : pseudoLegalMovesMin) {
        if (position.makeMove(moveMin, null, null)) {
          min = -1;
        }
        position.unmakeMove();
        if (min == -1) {
          break;
        }
      }
    } else {
      for (Move moveMin : pseudoLegalMovesMin) {
        List<Move> pseudoLegalMovesMax = new ArrayList<>();
        if (position.makeMove(moveMin, pseudoLegalMovesMax, null)) {
          int max = searchMax(nMoves - 1, position, pseudoLegalMovesMax);
          if (min == 0 || max < min) {
            min = max;
          }
        }
        position.unmakeMove();
        if (min == -1) {
          break;
        }
      }
    }
    if (min == 0) {
      Move nullMove = new NullMove();
      min = position.makeMove(nullMove, null, null) ? -1 : nMoves;
      position.unmakeMove();
    }
    return min;
  }

  @Override
  public String getSummary() {
    return String.format("Mate in %d", nMoves);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MateSearch.class.getSimpleName() + "[", "]").add(
        "nMoves=" + nMoves).add(super.toString()).toString();
  }
}

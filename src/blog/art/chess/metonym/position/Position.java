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

package blog.art.chess.metonym.position;

import blog.art.chess.metonym.move.Move;
import blog.art.chess.metonym.move.NullMove;
import blog.art.chess.metonym.piece.Piece;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

public class Position {

  private SortedMap<Square, Piece> board;
  private final SortedMap<Section, Piece> box;
  private boolean blackToMove;
  private Set<Square> castlingOrigins;
  private Square enPassantTarget;
  private final Deque<State> memory;

  public Position(Map<Square, Piece> board, Map<Section, Piece> box, boolean blackToMove,
      Set<Square> castlingOrigins, Square enPassantTarget) {
    Piece.validate(board, blackToMove, castlingOrigins, enPassantTarget);
    this.board = new TreeMap<>(
        Comparator.comparingInt(Square::getFile).thenComparingInt(Square::getRank));
    this.board.putAll(board);
    this.box = new TreeMap<>(
        Comparator.comparing(Section::isBlack).thenComparingInt(Section::getOrder));
    this.box.putAll(box);
    this.blackToMove = blackToMove;
    this.castlingOrigins = new HashSet<>(castlingOrigins);
    this.enPassantTarget = enPassantTarget;
    this.memory = new ArrayDeque<>();
  }

  public SortedMap<Square, Piece> getBoard() {
    return board;
  }

  public SortedMap<Section, Piece> getBox() {
    return box;
  }

  public boolean isBlackToMove() {
    return blackToMove;
  }

  public Set<Square> getCastlingOrigins() {
    return castlingOrigins;
  }

  public void setEnPassantTarget(Square enPassantTarget) {
    this.enPassantTarget = enPassantTarget;
  }

  public boolean isLegal(List<Move> pseudoLegalMoves) {
    for (Square origin : board.keySet()) {
      Piece piece = board.get(origin);
      if (piece.isBlack() == blackToMove) {
        if (!piece.generateMoves(origin, board, box, castlingOrigins, enPassantTarget,
            pseudoLegalMoves)) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean makeMove(Move move, List<Move> pseudoLegalMoves, StringBuilder lanBuilder) {
    memory.addFirst(new State(new TreeMap<>(board), blackToMove, new HashSet<>(castlingOrigins),
        enPassantTarget));
    boolean legal = move.doMake(this, lanBuilder);
    blackToMove = !blackToMove;
    if (legal) {
      legal = isLegal(pseudoLegalMoves);
    }
    if (lanBuilder != null) {
      if (legal) {
        List<Move> pseudoLegalMovesNext = pseudoLegalMoves;
        if (pseudoLegalMovesNext == null) {
          pseudoLegalMovesNext = new ArrayList<>();
          for (Square origin : board.keySet()) {
            Piece piece = board.get(origin);
            if (piece.isBlack() == blackToMove) {
              piece.generateMoves(origin, board, box, castlingOrigins, enPassantTarget,
                  pseudoLegalMovesNext);
            }
          }
        }
        boolean terminal = true;
        for (Move moveNext : pseudoLegalMovesNext) {
          if (makeMove(moveNext, null, null)) {
            terminal = false;
          }
          unmakeMove();
          if (!terminal) {
            break;
          }
        }
        int nChecks = 0;
        Move nullMove = new NullMove();
        makeMove(nullMove, null, null);
        for (Square origin : board.keySet()) {
          Piece piece = board.get(origin);
          if (piece.isBlack() == blackToMove) {
            if (!piece.generateMoves(origin, board, box, castlingOrigins, enPassantTarget, null)) {
              nChecks++;
            }
          }
        }
        unmakeMove();
        if (terminal) {
          if (nChecks > 0) {
            if (nChecks > 1) {
              for (int i = 0; i < nChecks; i++) {
                lanBuilder.append("+");
              }
            }
            lanBuilder.append("#");
          } else {
            lanBuilder.append("=");
          }
        } else {
          if (nChecks > 0) {
            for (int i = 0; i < nChecks; i++) {
              lanBuilder.append("+");
            }
          }
        }
      }
    }
    return legal;
  }

  public void unmakeMove() {
    State state = memory.removeFirst();
    blackToMove = state.isBlackToMove();
    enPassantTarget = state.getEnPassantTarget();
    castlingOrigins = state.getCastlingOrigins();
    board = state.getBoard();
  }

  public boolean isCheck() {
    Move nullMove = new NullMove();
    boolean check = !makeMove(nullMove, null, null);
    unmakeMove();
    return check;
  }

  public static String formatToString(Position position, Operation operation) {
    return Piece.formatToString(position.board, position.blackToMove, position.castlingOrigins,
        position.enPassantTarget, operation);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Position.class.getSimpleName() + "[", "]").add("board=" + board)
        .add("box=" + box).add("blackToMove=" + blackToMove)
        .add("castlingOrigins=" + castlingOrigins).add("enPassantTarget=" + enPassantTarget)
        .add("memory=" + memory).toString();
  }
}

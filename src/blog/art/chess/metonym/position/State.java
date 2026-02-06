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

import blog.art.chess.metonym.piece.Piece;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;

public final class State {

  private final SortedMap<Square, Piece> board;
  private final boolean blackToMove;
  private final Set<Square> castlingOrigins;
  private final Square enPassantTarget;

  public State(SortedMap<Square, Piece> board, boolean blackToMove, Set<Square> castlingOrigins,
      Square enPassantTarget) {
    this.board = board;
    this.blackToMove = blackToMove;
    this.castlingOrigins = castlingOrigins;
    this.enPassantTarget = enPassantTarget;
  }

  public SortedMap<Square, Piece> getBoard() {
    return board;
  }

  public boolean isBlackToMove() {
    return blackToMove;
  }

  public Set<Square> getCastlingOrigins() {
    return castlingOrigins;
  }

  public Square getEnPassantTarget() {
    return enPassantTarget;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", State.class.getSimpleName() + "[", "]").add("board=" + board)
        .add("blackToMove=" + blackToMove).add("castlingOrigins=" + castlingOrigins)
        .add("enPassantTarget=" + enPassantTarget).toString();
  }
}

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

import java.util.Objects;
import java.util.StringJoiner;

public final class Square {

  private final int file;
  private final int rank;

  public Square(int file, int rank) {
    this.file = file;
    this.rank = rank;
  }

  public int getFile() {
    return file;
  }

  public int getRank() {
    return rank;
  }

  public String getLanCode() {
    return "" + (char) ('a' + file - 1) + (char) ('1' + rank - 1);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Square)) {
      return false;
    }
    Square square = (Square) o;
    return file == square.file && rank == square.rank;
  }

  @Override
  public int hashCode() {
    return Objects.hash(file, rank);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Square.class.getSimpleName() + "[", "]").add("file=" + file)
        .add("rank=" + rank).toString();
  }
}

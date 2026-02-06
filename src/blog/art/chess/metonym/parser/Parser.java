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

package blog.art.chess.metonym.parser;

import blog.art.chess.metonym.piece.Bishop;
import blog.art.chess.metonym.piece.King;
import blog.art.chess.metonym.piece.Knight;
import blog.art.chess.metonym.piece.Pawn;
import blog.art.chess.metonym.piece.Piece;
import blog.art.chess.metonym.piece.Queen;
import blog.art.chess.metonym.piece.Rook;
import blog.art.chess.metonym.position.Position;
import blog.art.chess.metonym.position.Section;
import blog.art.chess.metonym.position.Square;
import blog.art.chess.metonym.problem.MateSearch;
import blog.art.chess.metonym.problem.Perft;
import blog.art.chess.metonym.problem.Problem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.MatchResult;

public class Parser {

  private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

  public static List<Problem> readAllProblems() {
    List<Problem> problems = new ArrayList<>();
    Scanner records = new Scanner(System.in);
    while (records.hasNextLine()) {
      String line = records.nextLine();
      if (!line.trim().isEmpty()) {
        try (Scanner fields = new Scanner(line)) {
          Map<String, Piece> pieces = new HashMap<>();
          Map<Square, Piece> board = new HashMap<>();
          try (Scanner characters = new Scanner(fields.next())) {
            characters.useDelimiter("");
            for (int rank = 8; rank >= 1; rank--) {
              for (int file = 1; file <= 8; file++) {
                if (characters.hasNext("[" + "12345678".substring(0, 8 - (file - 1)) + "]")) {
                  file += characters.nextInt();
                  if (file > 8) {
                    break;
                  }
                }
                String letter = characters.next("[KQRBNPkqrbnp]");
                switch (letter) {
                  case "K":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new King(false)));
                    break;
                  case "Q":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Queen(false)));
                    break;
                  case "R":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Rook(false)));
                    break;
                  case "B":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Bishop(false)));
                    break;
                  case "N":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Knight(false)));
                    break;
                  case "P":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Pawn(false)));
                    break;
                  case "k":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new King(true)));
                    break;
                  case "q":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Queen(true)));
                    break;
                  case "r":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Rook(true)));
                    break;
                  case "b":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Bishop(true)));
                    break;
                  case "n":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Knight(true)));
                    break;
                  case "p":
                    board.put(new Square(file, rank),
                        pieces.computeIfAbsent(letter, k -> new Pawn(true)));
                    break;
                }
              }
              characters.skip(rank > 1 ? "/" : "$");
            }
          }
          Map<Section, Piece> box = new HashMap<>();
          if (pieces.containsKey("P")) {
            box.put(new Section(false, 1), pieces.computeIfAbsent("Q", k -> new Queen(false)));
            box.put(new Section(false, 2), pieces.computeIfAbsent("R", k -> new Rook(false)));
            box.put(new Section(false, 3), pieces.computeIfAbsent("B", k -> new Bishop(false)));
            box.put(new Section(false, 4), pieces.computeIfAbsent("N", k -> new Knight(false)));
          }
          if (pieces.containsKey("p")) {
            box.put(new Section(true, 1), pieces.computeIfAbsent("q", k -> new Queen(true)));
            box.put(new Section(true, 2), pieces.computeIfAbsent("r", k -> new Rook(true)));
            box.put(new Section(true, 3), pieces.computeIfAbsent("b", k -> new Bishop(true)));
            box.put(new Section(true, 4), pieces.computeIfAbsent("n", k -> new Knight(true)));
          }
          boolean blackToMove = false;
          if (fields.hasNext("w")) {
            fields.next();
          } else {
            fields.next("b");
            blackToMove = true;
          }
          Set<Square> castlingOrigins = new HashSet<>();
          if (fields.hasNext("-")) {
            fields.next();
          } else {
            for (String letter : fields.next("\\bK?Q?k?q?").split("")) {
              if (letter.equals("K") || letter.equals("Q")) {
                castlingOrigins.add(new Square(5, 1));
              } else if (letter.equals("k") || letter.equals("q")) {
                castlingOrigins.add(new Square(5, 8));
              }
              if (letter.equals("K")) {
                castlingOrigins.add(new Square(8, 1));
              } else if (letter.equals("Q")) {
                castlingOrigins.add(new Square(1, 1));
              } else if (letter.equals("k")) {
                castlingOrigins.add(new Square(8, 8));
              } else if (letter.equals("q")) {
                castlingOrigins.add(new Square(1, 8));
              }
            }
          }
          Square enPassantTarget = null;
          if (fields.hasNext("-")) {
            fields.next();
          } else {
            fields.next("([a-h])([36])");
            MatchResult result = fields.match();
            int file = 1 + result.group(1).charAt(0) - 'a';
            int rank = 1 + result.group(2).charAt(0) - '1';
            enPassantTarget = new Square(file, rank);
          }
          switch (fields.next("acd|dm")) {
            case "acd":
              fields.next("(0|[1-9]\\d*);");
              int nPlies = Integer.parseInt(fields.match().group(1));
              fields.skip("\\s*$");
              problems.add(
                  new Perft(new Position(board, box, blackToMove, castlingOrigins, enPassantTarget),
                      nPlies));
              break;
            case "dm":
              fields.next("([1-9]\\d*);");
              int nMoves = Integer.parseInt(fields.match().group(1));
              fields.skip("\\s*$");
              problems.add(new MateSearch(
                  new Position(board, box, blackToMove, castlingOrigins, enPassantTarget), nMoves));
              break;
          }
        } catch (IllegalArgumentException ex) {
          LOGGER.warning(String.format("Not accepted line: '%s'. %s.", line, ex.getMessage()));
          return new ArrayList<>();
        } catch (NoSuchElementException ex) {
          LOGGER.warning(String.format("Invalid line: '%s'.", line));
          return new ArrayList<>();
        }
      }
    }
    return problems;
  }
}

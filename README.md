# Metonym

Metonym is a mate search chess program.

## Usage

Java 8 or later is required.

```
java -jar Metonym.jar [OPTIONS]
```

Metonym reads problems
as [Extended Position Description](https://www.chessprogramming.org/Extended_Position_Description)
records (with one operation: `dm` for direct mate or `acd` for perft) from standard input until
end-of-file, then solves them and writes solutions to standard output.

## Options

- `-h`, `--help` Shows help and exits.
- `-V`, `--version` Shows version and exits.
- `-d`, `--detailed` Enables detailed analysis.
- `-v`, `--verbose` Enables verbose logging.

## Example

> Sam Loyd, The Chess Monthly 1859

### Input

```
8/8/8/p7/8/8/R6p/2K2Rbk w - - dm 5;
```

### Output (default)

```
Ra2-f2 [#5]
```

### Output with `--detailed`

```
1.Ra2-f2 a5-a4 2.Kc1-d2 a4-a3 3.Rf1-a1 a3-a2 4.Kd2-e1 Bg1xf2+ 5.Ke1xf2#
```

## Author

Ivan Denkovski is the author of Metonym.

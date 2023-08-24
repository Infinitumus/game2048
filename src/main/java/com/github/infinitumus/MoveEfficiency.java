package com.github.infinitumus;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private final int numberOfEmptyTiles;
    private final int score;
    private final Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        int result = Integer.compare(this.numberOfEmptyTiles, o.numberOfEmptyTiles);
        if (result == 0) {
            result = Integer.compare(this.score, o.score);
        }
        return result;
    }
}

package com.github.infinitumus;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;

    Stack<Tile[][]> previousStates = new Stack<>();
    Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    int score;
    int maxTile;

    public Model() {
        resetGameTiles();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        if (priorityQueue.peek() != null) {
            priorityQueue.peek().getMove().move();
        }
    }

    private boolean hasBoardChanged() {
        return getTilesWeight(gameTiles) != getTilesWeight(previousStates.peek());
    }

    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency;
        move.move();
        if (!hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
            rollback();
            return moveEfficiency;
        }
        moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        rollback();
        return moveEfficiency;
    }

    private int getTilesWeight(Tile[][] tiles) {
        int weight = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                weight += tiles[i][j].value;
            }
        }
        return weight;
    }

    public void randomMove() {
        int n = (int) ((Math.random() * 100) % 4);
        switch (n) {
            case 0 -> left();
            case 1 -> right();
            case 2 -> up();
            case 3 -> down();
        }
    }

    private void saveState(Tile[][] gameTiles) {
        Tile[][] tiles = new Tile[gameTiles.length][gameTiles[0].length];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                tiles[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(tiles);
        previousScores.push(this.score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public boolean canMove() {
        /*boolean canMove = !getEmptyTiles().isEmpty();
        for (int i = 0; i < FIELD_WIDTH - 1; i++) {
            for (int j = 0; j < FIELD_WIDTH - 1; j++) {
                if ((gameTiles[i][j].value == gameTiles[i][j + 1].value) || (gameTiles[i][j].value == gameTiles[i + 1][j].value)) {
                    canMove = true;
                    break;
                }
            }
        }
        return canMove;*/

        // empty tiles exist -> can move
        if (!getEmptyTiles().isEmpty()) return true;

        // checking the first column and row for the same neighbours
        for (int i = 1; i < FIELD_WIDTH; i++) {
            if (gameTiles[0][i].value == gameTiles[0][i - 1].value ||
                    gameTiles[i][0].value == gameTiles[i - 1][0].value) {
                return true;
            }
        }

        //checking other tiles for the same neighbours
        for (int i = 1; i < FIELD_WIDTH; i++) {
            for (int j = 1; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == gameTiles[i - 1][j].value ||
                        gameTiles[i][j].value == gameTiles[i][j - 1].value
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resetGameTiles() {
        this.score = 0;
        this.maxTile = 0;
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (emptyTiles.isEmpty()) {
            return;
        }
        emptyTiles.get((int) (emptyTiles.size() * Math.random())).value =
                (Math.random() < 0.9 ? 2 : 4);
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length; j++) {
                Tile tile = gameTiles[i][j];
                if (tile.isEmpty()) {
                    emptyTiles.add(tile);
                }
            }
        }
        return emptyTiles;
    }

    private boolean compressTiles(Tile[] tiles) {
        Tile temp;
        int j = 0;
        boolean isChanged = false;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].value != 0) {
                if (i != j) {
                    temp = tiles[j];
                    tiles[j] = tiles[i];
                    tiles[i] = temp;
                    isChanged = true;
                }
                j++;
            }
        }
        return isChanged;
    }


    private boolean mergeTiles(Tile[] tiles) {
        boolean isChanged = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            int value = tiles[i].value;
            int value2 = tiles[i + 1].value;
            if (value != 0 && value == value2) {
                isChanged = true;
                value = value * 2;
                tiles[i].value = value;
                score += value;
                if (value > maxTile) {
                    maxTile = value;
                }
                tiles[i + 1].value = 0;
                i++;
            }
        }
        compressTiles(tiles);
        return isChanged;
    }

    void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean isChanged = false;
        for (Tile[] row : gameTiles) {
            if (compressTiles(row) | mergeTiles(row)) {
                isChanged = true;
            }
        }
        if (isChanged) {
            addTile();
        }
        isSaveNeeded = true;
    }

    void right() {
        saveState(gameTiles);
        rotateRight(gameTiles);
        rotateRight(gameTiles);
        left();
        rotateRight(gameTiles);
        rotateRight(gameTiles);
    }

    void up() {
        saveState(gameTiles);
        rotateRight(gameTiles);
        rotateRight(gameTiles);
        rotateRight(gameTiles);
        left();
        rotateRight(gameTiles);
    }

    void down() {
        saveState(gameTiles);
        rotateRight(gameTiles);
        left();
        rotateRight(gameTiles);
        rotateRight(gameTiles);
        rotateRight(gameTiles);
    }

    // На месте повернуть его на 90 градусов по часовой стрелке
    private static void rotateRight(Tile[][] tiles) {
        // базовый вариант
        if (tiles == null || tiles.length == 0) {
            return;
        }

        // Матрица `width × width`
        int width = tiles.length;

        // Транспонировать матрицу
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < i; j++) {
                Tile temp = tiles[i][j];
                tiles[i][j] = tiles[j][i];
                tiles[j][i] = temp;
            }
        }

        // поменять местами столбцы
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width / 2; j++) {
                Tile temp = tiles[i][j];
                tiles[i][j] = tiles[i][width - j - 1];
                tiles[i][width - j - 1] = temp;
            }
        }
    }

}


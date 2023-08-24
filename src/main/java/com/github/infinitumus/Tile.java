package com.github.infinitumus;

import java.awt.*;

public class Tile {
    int value;

    public Tile() {
        value = 0;
    }

    public Tile(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == 0;
    }

    public Color getFontColor() {
        return new Color(value < 16 ? 0x776e65 : 0xf9f6f2);
    }

    public Color getTileColor() {
        int color = switch (this.value) {
            case 0 -> (0xcdc1b4);
            case 2 -> (0xeee4da);
            case 4 -> (0xede0c8);
            case 8 -> (0xf2b179);
            case 16 -> (0xf59563);
            case 32 -> (0xf67c5f);
            case 64 -> (0xf65e3b);
            case 128 -> (0xedcf72);
            case 256 -> (0xedcc61);
            case 512 -> (0xedc850);
            case 1024 -> (0xedc53f);
            case 2048 -> (0xedc22e);
            default -> (0xff0000);
        };
        return new Color(color);
    }
}

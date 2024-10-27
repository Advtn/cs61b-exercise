package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873125;
    private static final Random RANDOM = new Random(SEED);


    /**
     * Draws a column of hexagons at Position p
     */
    public static void drawHexColumn(TETile[][] tiles, Position p, int length, int n) {
        for (int i = 0; i < length; i++) {
            addHexagon(tiles, p, n);
            p = getBottomNeighbor(p, n);
        }
    }

    /**
     * Gets the bottom hexagon neighbor position of position p
     * @param n is size of hexagon
     */
    private static Position getBottomNeighbor(Position p, int n) {
        return p.shift(0, -2*n);
    }

    /**
     * Gets the top and right hexagon neighbor position of position p
     * @param n is size of hexagon
     */
    private static Position getTopRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, n);
    }

    /**
     * Gets the bottom and right hexagon neighbor position of position p
     * @param n is size of hexagon
     */
    private static Position getBottomRightNeighbor(Position p, int n) {
        return p.shift(2*n - 1, -n);
    }

    /**
     *  Adds a hexagon of side SIZE to a given position in the world.
     */
    public static void addHexagon(TETile[][] tiles, Position p, int n) {
        if (n < 2) {
            return;
        }
        addHexagonHelper(tiles, p, randomTile(), n-1, n);
    }

    /**
     *  addHexagon helper method
     * @param b is num of blank
     * @param t is num of tile
     */
    private static void addHexagonHelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        Position startOfRow = p.shift(b, 0);
        // draw this row
        drawRow(tiles, startOfRow, tile, t);

        // draw the rest recursively
        if (b > 0) {
            Position nextP = p.shift(0, -1);
            addHexagonHelper(tiles, nextP, tile, b - 1, t + 2);
        }

        // draw this row of the reflection
        Position startOfReflectedRow = startOfRow.shift(0, -(2*b + 1));
        drawRow(tiles, startOfReflectedRow, tile, t);
    }

    /**
     * draws a row at Position p
     */
    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    /**
     * Picks a random ecology
     * @return
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.GRASS;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.TREE;
            case 4: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillWithNothingTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    /**
     * Draw a world
     */
    public static void drawWorld(TETile[][] tiles, Position p, int hexSize, int tessSize) {
        drawHexColumn(tiles, p, tessSize, hexSize);

        // extends up and right
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            drawHexColumn(tiles, p, tessSize + i, hexSize);
        }

        // extends down and right
        for (int i = tessSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            drawHexColumn(tiles, p, tessSize + i, hexSize);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothingTiles(world);
        Position anchor = new Position(12, 34);
        drawWorld(world, anchor, 4, 3);

        ter.renderFrame(world);
    }
}

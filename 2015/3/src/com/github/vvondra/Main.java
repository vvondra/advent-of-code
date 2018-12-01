package com.github.vvondra;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get("input"));
        String trip = new String(encoded);

        Set<Coord> visited = new HashSet<>();
        Coord current = new Coord(0, 0);
        visited.add(current);

        int houses = 1;

        for (char c : trip.toCharArray()) {
            current = current.travel(c);
            if (visited.add(current)) {
                houses++;
            }
        }

        System.out.println(houses);
    }

    public static class Coord {
        public final int x;
        public final int y;
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Coord travel(char dir) {
            switch (dir) {
                case '>':
                    return new Coord(x, y+1);
                case '<':
                    return new Coord(x, y-1);
                case 'v':
                    return new Coord(x+1, y);
                case '^':
                    return new Coord(x-1, y);
                default:
                    return this;

            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Coord coord = (Coord) o;
            return x == coord.x &&
                y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}

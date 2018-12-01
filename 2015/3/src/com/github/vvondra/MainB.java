package com.github.vvondra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainB {

    public static void main(String[] args) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get("input"));
        String trip = new String(encoded);

        Set<Coord> visited = new HashSet<>();
        Coord currentSanta = new Coord(0, 0);
        Coord currentRobo = new Coord(0, 0);
        visited.add(currentSanta);
        visited.add(currentRobo);

        int houses = 1;
        boolean santaTurn = true;

        for (char c : trip.toCharArray()) {
            Coord current;
            if (santaTurn) {
                currentSanta = currentSanta.travel(c);
                current = currentSanta;
            } else {
                currentRobo = currentRobo.travel(c);
                current = currentRobo;
            }

            if (visited.add(current)) {
                houses++;
            }

            santaTurn = !santaTurn;
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

import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("13.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const arcade = new Program(input, []);

enum Tile {
  Empty = 0,
  Wall = 1,
  Block = 2,
  Paddle = 3,
  Ball = 4
}

(async () => {
  const grid: Tile[][] = [];
  const counter = { 0: 0, 1: 0, 2: 0, 3: 0, 4: 0};
  const set = (x: number, y: number, z: number) => {
    if (!grid[y]) {
      grid[y] = [];
    }

    grid[y][x] = z;
    counter[z]++;
  }

  while (true) {
    const x = await arcade.next();
    if (x.done) {
      break;
    }
    const y = await arcade.next();
    const z = await arcade.next();

    set(x.value, y.value, z.value);
  }

  console.log(counter[Tile.Block]);
})();

import * as fs from "fs";
import Program from "./intcode";
import * as readline from "readline";

const input = fs.readFileSync("13.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);
input[0] = 2;

const arcade = new Program(input, []);

enum Tile {
  Empty = 0,
  Wall = 1,
  Block = 2,
  Paddle = 3,
  Ball = 4
}

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const render = (game: Tile[][], score: number, other = "") => {
  const format = (t: Tile) => {
    switch (t) {
      case Tile.Empty: return " ";
      case Tile.Ball: return "O";
      case Tile.Block: return "▓";
      case Tile.Wall: return "#";
      case Tile.Paddle: return "=";
    }
    return "?";
  }
  const cols = Math.max(...game.map(r => r.length));
  const blank = '\n'.repeat(process.stdout.rows)
  console.log(blank)
  readline.cursorTo(process.stdout, 0, 0)
  readline.clearScreenDown(process.stdout)
  for (let r = 0; r < game.length; r++) {
    for (let c = 0; c < cols; c++) {
      process.stdout.write(format(game[r][c]));
    }
    process.stdout.write("\n");
  }
  process.stdout.write("Score: " + score + "\n");
  console.log(other);
}

(async () => {
  const grid: Tile[][] = [];
  let score = 0;
  const counter = { 0: 0, 1: 0, 2: 0, 3: 0, 4: 0};
  let ball = [0, 0];
  let paddle = [0, 0];
  const set = (x: number, y: number, z: number) => {
    if (!grid[y]) {
      grid[y] = [];
    }

    if (z === Tile.Ball) {
      ball = [x, y];
    }
    if (z === Tile.Paddle) {
      paddle = [x, y];
    }
    grid[y][x] = z;
    counter[z]++;
  }

  arcade.inputFn = async () => {
    render(grid, score)
    await sleep(5);
    return Math.sign(ball[0] - paddle[0]);
  };

  while (true) {
    const x = await arcade.next();
    if (x.done) {
      break;
    }

    const y = await arcade.next();
    const z = await arcade.next();

    if (x.value === -1 && y.value === 0) {
      score = z.value;
    }
    set(x.value, y.value, z.value);
  }

  render(grid, score)
  console.log(counter[Tile.Block]);
})();

import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("11.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const mod = (a: number, b: number) => ((a % b) + b) % b;

enum Color {
  Black = 0,
  White = 1,
}

type Coord = { x: number, y: number };
enum Rotation { Up = 0, Right = 1, Down = 2, Left = 3 }
const rotationMove = {
  [Rotation.Up]: (c: Coord) => { c.y++; return c; },
  [Rotation.Right]: (c: Coord) => { c.x++; return c; },
  [Rotation.Down]: (c: Coord) => { c.y--; return c; },
  [Rotation.Left]: (c: Coord) => { c.x--; return c; },
};

const paint = async (start: Color) => {
  const painted = {};
  const coord = { x: 0, y: 0};
  let rotation = 0;
  let colorMode = true;
  const robot = new Program(input, [start]);

  for await (const value of robot.process) {
    if (colorMode) {
      if (value === Color.White) {
        painted[coord.x + ":" + coord.y] = Color.White;
      } else if (value === Color.Black) {
        painted[coord.x + ":" + coord.y] = Color.Black;
      } else {
        throw new Error("Unexpected color");
      }
    } else {
      if (value === 0) {
        rotation = mod(rotation - 1, 4);
      } else if (value === 1) {
        rotation = mod(rotation + 1, 4);
      } else {
        throw new Error("Unexpected rotation");
      }
      rotationMove[rotation](coord);

      robot.addInput(painted[coord.x + ":" + coord.y] || Color.Black);
    }

    colorMode = !colorMode;
  }

  return painted;
};

paint(Color.Black).then(p => console.log(Object.keys(p).length));
paint(Color.White).then(p => {
  const painting = [];
  let bottomBounds = [Infinity, Infinity];
  let topBounds = [-Infinity, -Infinity];
  for (const key in p) {
    const coords = key.split(":").map(n => parseInt(n, 10));
    if (!painting[coords[1]]) {
      painting[coords[1]] = [];
    }

    painting[coords[1]][coords[0]] = p[key];
    bottomBounds = [Math.min(bottomBounds[0], coords[0]), Math.min(bottomBounds[1], coords[1])];
    topBounds = [Math.max(topBounds[0], coords[0]), Math.max(topBounds[1], coords[1])];
  }

  for (let c = topBounds[1]; c >= bottomBounds[1]; c--) {
    for (let r = bottomBounds[0]; r < topBounds[0] + 1; r++) {
        process.stdout.write((painting[c] && painting[c][r]) ? "▓" : " ");
    }
    process.stdout.write("\n");
  }
});

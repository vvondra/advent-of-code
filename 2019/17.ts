import * as fs from "fs";
import Program from "./intcode";
import { isNumber } from "util";

const input = fs.readFileSync("17.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const load = async (inp: number[]): Promise<string[][]> => {
  const p = new Program(inp);
  const output = [];
  for await (const out of p.process) {
    output.push(out)
  }

  const scaffold = output.reduce(([row, grid], el) => {
    const char = String.fromCharCode(el);
    if (char === "\n") {
      grid[row + 1] = [];
      return [row + 1, grid];
    }

    grid[row].push(char);
    return [row, grid];
  }, [0, [[]]])[1].filter((e: string[]) => e.length > 0);

  return scaffold;
}

const render = (scaffold: string[][]) => {
  for (let row = 0; row < scaffold.length; row++) {
    for (let col = 0; col < scaffold[row].length; col++) {
      process.stdout.write(scaffold[row][col]);
    }
    process.stdout.write("\n");
  }
}

const crossings = (scaffold: string[][]) => {
  let out = [];
  const offsets = [[0, 1], [0, -1], [1, 0], [-1, 0], [0, 0]];
  for (let row = 1; row < scaffold.length - 1; row++) {
    for (let col = 1; col < scaffold[row].length - 1; col++) {
      if (offsets.every(o => scaffold[row + o[0]][col + o[1]] === "#")) {
        out.push([row, col]);
      }
    }
  }

  return out;
}

const alignment = (list: number[][]): number => {
  return list.reduce((alignment, cross) => {
    return alignment + (cross[1] * cross[0]);
  }, 0)
}

const transpose = <T>(m: T[][]): T[][] => m[0].map((x,i) => m.map(x => x[i]));
const lengths = (list: string[][]): number[] => {
  const out = [];
  let buffer = 0;
  for (let row = 0; row < list.length; row++) {
    for (let col = 0; col < list[row].length; col++) {
      if (list[row][col] !== ".") {
        buffer++;
      } else if (buffer > 0) {
        out.push(buffer);
        buffer = 0;
      }
    }

    if (buffer > 0) {
      out.push(buffer);
      buffer = 0;
    }
  }

  return out.filter(x => x != 1);
}

const findRobot = (list: string[][]): [number, number] => {
  return list.reduce((index, row, currentRow) => {
    const pos = [
      row.indexOf("v"),
      row.indexOf("^"),
      row.indexOf(">"),
      row.indexOf("<")
    ].filter(i => i > -1);
    if (pos.length > 0) {
      return [currentRow, pos[0]];
    }
    return index;
  }, [0, 0] as [number, number]);
}

const mod = (a: number, b: number) => ((a % b) + b) % b;
const rotations = {
  0: [-1, 0],
  1: [0, 1],
  2: [1, 0],
  3: [0, -1]
}
const rotationChars = {
  '^': 0,
  '>': 1,
  'v': 2,
  '<': 3
}
const nextRotation = (list: string[][], current: [number, number], rotation: number) => {
  const left = mod(rotation - 1, 4);
  const leftPos = [current[0] + rotations[left][0], current[1] + rotations[left][1]];
  if (list[leftPos[0]][leftPos[1]] === '#') {
    return ['L', left];
  }

  const right = mod(rotation + 1, 4);
  const rightPos = [current[0] + rotations[right][0], current[1] + rotations[right][1]];
  if (list[rightPos[0]][rightPos[1]] === '#') {
    return ['R', right];
  }

  throw new Error("illegal state");
}
const sequence = (list: string[][]): Array<string | number> => {
  let current = findRobot(list);

  console.log(nextRotation(list, current, rotationChars[list[current[0]][current[1]]]));

  return [];
}

(async () => {
  const scaffold = await load(input);

  console.log(alignment(crossings(scaffold)));
  console.log(lengths(scaffold));
  console.log(lengths(transpose(scaffold)));
  console.log(sequence(scaffold));
})();

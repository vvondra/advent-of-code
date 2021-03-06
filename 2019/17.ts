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

  return createScaffold(output);
}

const createScaffold = (output: number[]): string[][] => {
  const scaffold: string[][] = output.reduce(([row, grid]: [number, string[][]], el) => {
    const char = el > 255 ? el.toString() : String.fromCharCode(el);
    if (char === "\n") {
      grid[row + 1] = [];
      return [row + 1, grid];
    }

    grid[row].push(char);
    return [row, grid];
  }, [0, [[]]])[1] as string[][];

  return scaffold.filter((e: string[]) => e.length > 0);
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
const nextRotation = (list: string[][], current: [number, number], rotation: number): [string, number] => {
  const left = mod(rotation - 1, 4);
  const leftPos = [current[0] + rotations[left][0], current[1] + rotations[left][1]];
  if (list[leftPos[0]] && list[leftPos[0]][leftPos[1]] === '#') {
    return ['L', left];
  }

  const right = mod(rotation + 1, 4);
  const rightPos = [current[0] + rotations[right][0], current[1] + rotations[right][1]];
  if (list[rightPos[0]] && list[rightPos[0]][rightPos[1]] === '#') {
    return ['R', right];
  }

  return ['X', rotation];
}
const sequence = (list: string[][]): Array<string | number> => {
  let current = findRobot(list);
  let instructions = [];
  let rotation = rotationChars[list[current[0]][current[1]]];
  let step: string;
  while (true) {
    [step, rotation] = nextRotation(list, current, rotation);
    if (step === 'X') {
      break;
    }
    instructions.push(step);
    const vector = rotations[rotation];
    let length = 0;
    while (list[current[0] + vector[0]] && list[current[0] + vector[0]][current[1] + vector[1]] === "#") {
      length++;
      current = [current[0] + vector[0], current[1] + vector[1]]
    }
    instructions.push(length);
  }

  return instructions;
}

(async () => {
  const scaffold = await load(input);

  console.log(alignment(crossings(scaffold)));
  render(scaffold)
  const instructions = sequence(scaffold).join(":");

  // Cheater, cheater :( using only brain power here
  // I generated possibilities using another function and then guessed some last chars
  // Probably I would have to code some function trying segments of different sizes
  // Or perhaps a suffix tree would work
  const a = /R:8:L:10:R:8/g;
  const b = /R:12:R:8:L:8:L:12/g;
  const c = /L:12:L:10:L:8/g;
  const prog = instructions
    .replace(a, "A")
    .replace(b, "B")
    .replace(c, "C")
    .split(":")
    .join(",");
  const fns = [
    a.source,
    b.source,
    c.source
  ].map(fn => fn.split(":").join(","));

  const seq = [prog, ...fns].join("\n")
  const ascii = seq.split("")
    .map(c => c.charCodeAt(0))
    .concat(["\n".charCodeAt(0)])
    .concat(["n".charCodeAt(0)])
    .concat(["\n".charCodeAt(0)])
    ;

  const input2 = input.slice(0);
  input2[0] = 2;
  const robot = new Program(input2, ascii);
  const output = []
  for await (const out of robot.process) {
    output.push(out);
  }

  render(createScaffold(output));
})();

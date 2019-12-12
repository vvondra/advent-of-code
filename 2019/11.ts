import * as fs from "fs";

const input = fs.readFileSync("11.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(BigInt);

type OpResult = {
  state: Array<bigint>,
  ip: number,
  rp: number,
};

enum Mode {
  Position = 0,
  Immediate = 1,
  Relative = 2,
}

class Op {
  op: number;
  ip: number;
  rp: number;
  params: Array<bigint>;
  modes: number[];

  constructor(prev: Array<bigint>, ip: number, rp: number) {
    const code = prev[ip];
    this.ip = ip;
    this.rp = rp;
    this.op = Number(code) % 100;
    this.modes = Math.trunc(Number(code) / 100).toString().split("").map(Number).reverse();
    this.params = prev.slice(ip + 1, ip + 1 + this.paramCount());
  }

  paramCount() {
    return {
      1: 3,
      2: 3,
      3: 1,
      4: 1,
      5: 2,
      6: 2,
      7: 3,
      8: 3,
      9: 1,
      99: 0,
    }[this.op];
  }

  value(position: number, state: Array<bigint>, writeMode = false): bigint {
    const mode = this.modes[position] || Mode.Position;

    if (mode === Mode.Position) {
      if (writeMode) {
        return this.params[position];
      }
      return state[Number(this.params[position])] || BigInt(0);
    } else if (mode === Mode.Immediate) {
      return this.params[position];
    } else if (mode === Mode.Relative) {
      if (writeMode) {
        return this.params[position] + BigInt(this.rp);
      }
      return state[Number(this.params[position]) + this.rp] || BigInt(0);
    }
  }

  nextStep() {
    return this.ip + this.paramCount() + 1;
  }

  async execute(state: Array<bigint>, inputs: Array<bigint>, outputs: Array<bigint>): Promise<OpResult> {
    const updated = (memory: Array<bigint>, idx: bigint | number, value: bigint | number): Array<bigint> => {
      const copy = memory.slice();
      copy[Number(this.value(Number(idx), copy, true))] = BigInt(value);

      return copy;
    };

    return new Promise((resolve, reject) => {
      const result = {
        state,
        ip: this.nextStep(),
        rp: this.rp,
      };

      switch (this.op) {
        case 1:
          resolve({ ...result, state: updated(state, 2, this.value(0, state) + this.value(1, state))});
          break;
        case 2:
          result.state = updated(state, 2, this.value(0, state) * this.value(1, state));
          break;
        case 3:
          result.state = updated(state, 0, inputs.shift());
          break;
        case 4:
          outputs.push(this.value(0, state));
          break;
        case 5:
          if (this.value(0, state) !== BigInt(0)) {
            result.ip = Number(this.value(1, state));
          }
          break;
        case 6:
          if (this.value(0, state) === BigInt(0)) {
            result.ip = Number(this.value(1, state));
          }
          break;
        case 7:
          result.state = updated(state, 2, this.value(0, state) < this.value(1, state) ? 1 : 0);
          break;
        case 8:
          result.state = updated(state, 2, this.value(0, state) === this.value(1, state) ? 1 : 0);
          break;
        case 9:
          result.rp = result.rp + Number(this.value(0, state));
          break;
        case 99:
          result.ip = this.ip;
          break;
        default:
          reject(new Error("Unexpected op" + this.op));
          return;
      }

      resolve(result);
    });
  }
}

class Program {
  state: Array<bigint>;
  ip = 0;
  rp = 0;
  inputs: Array<bigint>;
  process: AsyncGenerator<bigint>;

  constructor(program: Array<bigint>, inputs: Array<bigint>) {
    this.state = program.slice(0);
    this.inputs = inputs.slice(0);
    this.process = this.run();
  }

  addInput(i: bigint) {
    this.inputs.push(i);
  }

  async next(): Promise<IteratorResult<bigint, any>> {
    return this.process.next();
  }

  private async * run(): AsyncGenerator<bigint> {
    while (this.state[this.ip] !== BigInt(99)) {
      const outputs = [];
      const result = await (new Op(this.state, this.ip, this.rp).execute(this.state, this.inputs, outputs));
      this.ip = result.ip;
      this.rp = result.rp;
      this.state = result.state;

      while (outputs.length > 0) {
        yield outputs.shift();
      }
    }
  }
}

const mod = (a: number, b: number) => ((a % b) + b) % b;

enum Color {
  Black = 0,
  White = 1
}

type Coord = { x: number, y: number };
enum Rotation { Up = 0, Right = 1, Down = 2, Left = 3 };
const rotationMove = {
  [Rotation.Up]: (c: Coord) => { c.y++; return c; },
  [Rotation.Right]: (c: Coord) => { c.x++; return c; },
  [Rotation.Down]: (c: Coord) => { c.y--; return c; },
  [Rotation.Left]: (c: Coord) => { c.x--; return c; },
}

const paint = async (start: Color) => {
  const painted = {};
  const coord = { x: 0, y: 0};
  let rotation = 0;
  let colorMode = true;
  const robot = new Program(input, [BigInt(start)]);

  for await (const value of robot.process) {
    if (colorMode) {
      if (value === BigInt(Color.White)) {
        painted[coord.x + ":" + coord.y] = Color.White;
      } else if (value === BigInt(Color.Black)) {
        painted[coord.x + ":" + coord.y] = Color.Black;
      } else {
        throw new Error("Unexpected color");
      }
    } else {
      if (value === BigInt(0)) {
        rotation = mod(rotation - 1, 4)
      } else if (value === BigInt(1)) {
        rotation = mod(rotation + 1, 4);
      } else {
        throw new Error("Unexpected rotation");
      }
      rotationMove[rotation](coord)

      robot.addInput(BigInt(painted[coord.x + ":" + coord.y] || Color.Black))
    }

    colorMode = !colorMode;
  }

  return painted;
}

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
        process.stdout.write((painting[c] && painting[c][r]) ? '▓' : ' ')
    }
    process.stdout.write("\n");
  }
});
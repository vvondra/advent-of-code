import * as fs from "fs";
import * as readline from "readline";

const input = fs.readFileSync("5.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(s => parseInt(s, 10));

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

class Op {
  op: number;
  ip: number;
  params: number[];
  modes: number[];

  constructor(prev: number[], ip: number) {
    const code = prev[ip];
    this.ip = ip;
    this.op = code % 100;
    this.modes = Math.trunc(code / 100).toString().split("").map(Number).reverse();
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
      99: 0,
    }[this.op];
  }

  value(position: number, state: number[]) {
    const mode = this.modes[position] || 0;

    if (mode === 0) {
      return state[this.params[position]];
    } else if (mode === 1) {
      return this.params[position];
    }
  }

  nextStep() {
    return this.ip + this.paramCount() + 1;
  }

  async execute(state: number[]): Promise<[number[], number]> {
    return new Promise((resolve, reject) => {
      switch (this.op) {
        case 1:
          resolve([
            Object.assign([], state, { [this.params[2]]: this.value(0, state) + this.value(1, state) }),
            this.nextStep(),
          ]);
          break;
        case 2:
          resolve([
            Object.assign([], state, { [this.params[2]]: this.value(0, state) * this.value(1, state) }),
            this.nextStep(),
          ]);
          break;
        case 3:
          rl.question("Input? ", (answer) => {
            resolve([
              Object.assign([], state, { [this.params[0]]: parseInt(answer, 10) }),
              this.nextStep(),
            ]);
            rl.close();
          });
          break;
        case 4:
          console.log(this.value(0, state));
          resolve([state, this.nextStep()]);
          break;
        case 5:
          if (this.value(0, state) !== 0) {
            resolve([state, this.value(1, state)]);
          } else {
            resolve([state, this.nextStep()]);
          }
          break;
        case 6:
          if (this.value(0, state) === 0) {
            resolve([state, this.value(1, state)]);
          } else {
            resolve([state, this.nextStep()]);
          }
          break;
        case 7:
          resolve([
            Object.assign([], state, { [this.params[2]]: (this.value(0, state) < this.value(1, state) ? 1 : 0) }),
            this.nextStep(),
          ]);
          break;
        case 8:
          resolve([
            Object.assign([], state, { [this.params[2]]: (this.value(0, state) === this.value(1, state) ? 1 : 0) }),
            this.nextStep(),
          ]);
          break;
        case 99:
          resolve([state, this.ip]);
          break;
        default:
          reject(new Error("Unexpected op" + this.op));
          return;
      }
    });
  }
}

const run = async (program: number[]) => {
  let ip = 0;
  let state = program.slice(0);
  while (state[ip] !== 99) {
    [state, ip] = await (new Op(state, ip).execute(state));
  }

  rl.close();

  return state;
};

run(input);

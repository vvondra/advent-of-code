import * as fs from "fs";

const input = fs.readFileSync("9.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(BigInt);

type OpResult = {
  state: bigint[],
  ip: number,
  rp: number
}

class Op {
  op: number;
  ip: number;
  rp: number;
  params: bigint[];
  modes: number[];

  constructor(prev: bigint[], ip: number, rp: number) {
    const code = prev[ip];
    this.ip = ip;
    this.rp = rp;
    this.op = Number(code) % 100;
    this.modes = Math.trunc(Number(code) / 100).toString().split("").map(Number).reverse();
    this.params = prev.slice(ip + 1, ip + 1 + this.paramCount());
    console.log(this.params, code, ip, prev);
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

  value(position: number, state: bigint[]) {
    const mode = this.modes[position] || 0;

    if (mode === 0) {
      return state[Number(this.params[position])];
    } else if (mode === 1) {
      return this.params[position];
    } else if (mode === 2) {
      return state[Number(this.params[position]) + this.rp];
    }
  }

  nextStep() {
    return this.ip + this.paramCount() + 1;
  }

  async execute(state: bigint[], inputs: bigint[], outputs: bigint[]): Promise<OpResult> {
    return new Promise((resolve, reject) => {
      const result = {
        state: state,
        ip: this.nextStep(),
        rp: this.rp
      }

      switch (this.op) {
        case 1:
          console.log("a", this.value(0, state), this.value(1, state))
          result.state = Object.assign([], state, { [Number(this.value(2, state))]: this.value(0, state) + this.value(1, state) });
          break;
        case 2:
          result.state = Object.assign([], state, { [Number(this.value(2, state))]: this.value(0, state) * this.value(1, state) });
          break;
        case 3:
          result.state = Object.assign([], state, { [Number(this.value(0, state))]: inputs.shift() });
          break;
        case 4:
          outputs.push(this.value(0, state));
          break;
        case 5:
          if (this.value(0, state) !== BigInt(0)) {
            result.ip = this.value(1, state);
          }
          break;
        case 6:
          if (this.value(0, state) === BigInt(0)) {
            result.ip = this.value(1, state);
          }
          break;
        case 7:
          result.state = Object.assign([], state, { [Number(this.value(2, state))]: (this.value(0, state) < this.value(1, state) ? 1 : 0) });
          break;
        case 8:
          result.state = Object.assign([], state, { [Number(this.value(2, state))]: (this.value(0, state) === this.value(1, state) ? 1 : 0) });
          break;
        case 9:
          result.rp = this.rp + Number(this.value(0, state));
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
  state: bigint[];
  ip = 0;
  rp = 0;
  inputs: bigint[];
  process: AsyncGenerator<bigint>;

  constructor(program: bigint[], inputs: bigint[]) {
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

const p = new Program(input, [BigInt(1)]);

(async () => {
  for await (let value of p.process) {
    console.log(value);
  }
})();

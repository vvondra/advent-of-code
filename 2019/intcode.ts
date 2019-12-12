
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

export default class Program {
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


type OpResult = {
  state: number[],
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
  params: number[];
  modes: number[];

  constructor(prev: number[], ip: number, rp: number) {
    const code = prev[ip];
    this.ip = ip;
    this.rp = rp;
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
      9: 1,
      99: 0,
    }[this.op];
  }

  value(position: number, state: number[], writeMode = false): number {
    const mode = this.modes[position] || Mode.Position;

    if (mode === Mode.Position) {
      if (writeMode) {
        return this.params[position];
      }
      return state[this.params[position]] || 0;
    } else if (mode === Mode.Immediate) {
      return this.params[position];
    } else if (mode === Mode.Relative) {
      if (writeMode) {
        return this.params[position] + this.rp;
      }
      return state[this.params[position] + this.rp] || 0;
    }
  }

  nextStep() {
    return this.ip + this.paramCount() + 1;
  }

  async execute(state: number[], inputs: () => Promise<number>, outputs: number[]): Promise<OpResult> {
    const updated = (memory: number[], idx: number | number, value: number | number): number[] => {
      const copy = memory.slice();
      copy[this.value(idx, copy, true)] = value;

      return copy;
    };

    const result = {
      state,
      ip: this.nextStep(),
      rp: this.rp,
    };

    switch (this.op) {
      case 1:
        return {
          ...result,
          state: updated(state, 2, this.value(0, state) + this.value(1, state)),
        };
      case 2:
        return {
          ...result,
          state: updated(state, 2, this.value(0, state) * this.value(1, state)),
        };
      case 3:
        return {
          ...result,
          state: updated(state, 0, await inputs()),
        };
      case 4:
        outputs.push(this.value(0, state));
        return result;
      case 5:
        if (this.value(0, state) !== 0) {
          return { ...result, ip: this.value(1, state) };
        }
        return result;
      case 6:
        if (this.value(0, state) === 0) {
          return { ...result, ip: this.value(1, state) };
        }
        return result;
      case 7:
        return {
          ...result,
          state: updated(state, 2, this.value(0, state) < this.value(1, state) ? 1 : 0),
        };
      case 8:
        return {
          ...result,
          state: updated(state, 2, this.value(0, state) === this.value(1, state) ? 1 : 0),
        };
      case 9:
        return {
          ...result,
          rp: result.rp + this.value(0, state),
        };
      case 99:
        return {
          ...result,
          ip: this.ip,
        };
      default:
        throw new Error("Unexpected op" + this.op);
    }
  }
}

export default class Program {
  state: number[];
  ip = 0;
  rp = 0;
  inputs: number[];
  inputFn: () => Promise<number>;
  process: AsyncGenerator<number>;

  constructor(program: number[], inputs: number[] = []) {
    this.state = program.slice(0);
    this.inputs = inputs.slice(0);
    this.process = this.run();
    this.inputFn = () => Promise.resolve(this.inputs.shift());
  }

  addInput(i: number) {
    this.inputs.push(i);
    return this;
  }

  dup(): Program {
    const p = new Program(this.state.slice(0), this.inputs)

    p.ip = this.ip;
    p.rp = this.rp;

    return p;
  }

  async next(): Promise<IteratorResult<number, any>> {
    return this.process.next();
  }

  async nextVal(): Promise<number> {
    return (await this.process.next()).value;
  }

  private async * run(): AsyncGenerator<number> {
    while (this.state[this.ip] !== 99) {
      const outputs = [];
      const result = await (new Op(this.state, this.ip, this.rp).execute(this.state, this.inputFn, outputs));
      this.ip = result.ip;
      this.rp = result.rp;
      this.state = result.state;

      while (outputs.length > 0) {
        yield outputs.shift();
      }
    }
  }
}

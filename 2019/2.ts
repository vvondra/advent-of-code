import * as fs from "fs";

const input = fs.readFileSync("2.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(s => parseInt(s, 10));

const execute = (prev: number[], position: number) => {
  const [op, a, b, out] = prev.slice(position);

  switch (op) {
    case 1:
      return Object.assign([], prev, { [out]: prev[a] + prev[b] });
    case 2:
      return Object.assign([], prev, { [out]: prev[a] * prev[b] });
    case 99:
      return prev;
    default:
      throw new Error("Unexpected op");
  }
};

const run = (program: number[]) => {
  let ip = 0;
  let state = program.slice(0);
  const step = 4;
  while (state[ip] !== 99) {
    state = execute(state, ip);
    ip += step;
  }

  return state;
};

const findInputs = (program: number[], target: number) => {
  for (let noun = 0; noun < 100; noun++) {
    for (let verb = 0; verb < 100; verb++) {
      if (run(Object.assign([], input, { 1: noun, 2: verb }))[0] === target) {
        return 100 * noun + verb;
      }
    }
  }
};

const assisted = run(Object.assign([], input, { 1: 12, 2: 2 }));
console.log(assisted[0]);
console.log(findInputs(input, 19690720));

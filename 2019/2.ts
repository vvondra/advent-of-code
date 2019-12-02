import * as fs from 'fs';

const input = fs.readFileSync('2.input', 'utf-8')
  .split(',')
  .map(s => s.trim())
  .map(s => parseInt(s, 10));

const execute = (prev: Array<number>, position: number) => {
  const [op, a, b, out] = prev.slice(position);
  console.log(out)
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
}

const run = (program: Array<number>) => {
  let ip = 0;
  let state = input.slice(0);
  const step = 4;
  while (state[ip] != 99) {
    state = execute(state, ip);
    ip += step;
  }

  return state;
}


console.log(input)
console.log(Object.assign([], input, { 1: 12, 2: 2 }));

const assisted = run(Object.assign([], input, { 1: 12, 2: 2 }));
//console.log(assisted[0]);


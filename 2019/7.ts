import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("7.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(BigInt);

const runAmplifiers = async (program: bigint[], phases: bigint[]): Promise<bigint> => {
  const feed = 0;
  const amplifiers: Program[] = [];
  for (let i = 0; i < phases.length; i++) {
    amplifiers[i] = new Program(program, [phases[i]]);
  }
  amplifiers[0].addInput(BigInt(0));

  let current = 0;
  let lastOutput: bigint = (await amplifiers[current].next()).value;

  while (true) {
    current = (current + 1) % phases.length;
    amplifiers[current].addInput(lastOutput);
    const output = await amplifiers[current].next();
    if (output.done) {
      break;
    }

    lastOutput = output.value;
  }

  return lastOutput;
};

const permutations = <T>(xs: T[]): T[][] => {
  const ret = [];

  for (let i = 0; i < xs.length; i = i + 1) {
    const rest = permutations(xs.slice(0, i).concat(xs.slice(i + 1)));

    if (!rest.length) {
      ret.push([xs[i]]);
    } else {
      for (const r of rest) {
        ret.push([xs[i]].concat(r));
      }
    }
  }

  return ret;
};

const findMaxSignal = async (phases: bigint[], program: bigint[]) => {
  return Promise.all(permutations(phases).map(p => runAmplifiers(program, p)))
      .then(values => values.reduce((min, n) => n > min ? n : min, -Infinity));
};

findMaxSignal([0, 1, 2, 3, 4].map(BigInt), input).then(v => console.log(v.toString()));
findMaxSignal([5, 6, 7, 8, 9].map(BigInt), input).then(v => console.log(v.toString()));

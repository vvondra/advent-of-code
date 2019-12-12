import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("9.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(BigInt);

const p = new Program(input, [BigInt(1)]);
const p2 = new Program(input, [BigInt(2)]);

(async () => {
  for await (const value of p.process) {
    console.log(value.toString());
  }

  for await (const value of p2.process) {
    console.log(value.toString());
  }
})();

import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("5.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(BigInt);

const first = new Program(input, [BigInt(1)]);
const second = new Program(input, [BigInt(5)]);

(async () => {
  for await (const out of first.process) {
    console.log(out.toString());
  }

  for await (const out of second.process) {
    console.log(out.toString());
  }
})();

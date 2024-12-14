import * as fs from "fs";
import Program from "./intcode";

const input = fs.readFileSync("../2021/07.input", "utf-8")
  .split(",")
  .map(s => s.trim())
  .map(Number);

const p = new Program(input, []);

(async () => {
  for await (let value of p.process) {
    console.log(String.fromCharCode(value))
  }
})();
extern crate regex;

use std::io::BufReader;
use std::io::BufRead;
use std::fs::File;
use std::collections::HashMap;
use std::cell::UnsafeCell;

use regex::Regex;

struct Wire {
    name: String,
    source: Source
}

#[derive(Debug)]
enum Source {
    Charge(u16),
    Wire(Box<String>),
    Not(Box<Source>),
    And(Box<Source>, Box<Source>),
    Or(Box<Source>, Box<Source>),
    Xor(Box<Source>, Box<Source>),
    Rshift(Box<Source>, Box<Source>),
    Lshift(Box<Source>, Box<Source>)
}

struct Circuit {
    wires: HashMap<String, Wire>,
    value_cache: UnsafeCell<HashMap<String, u16>>,
}

impl Circuit {
    fn new() -> Circuit {
        Circuit {
            wires: HashMap::new(),
            value_cache: UnsafeCell::new(HashMap::new()),
        }
    }

    fn get_wire(&self, wire_name: String) -> &Wire {
        self.wires.get(&wire_name.to_string()).unwrap()
    }

    fn wire_charge(&self, wire_name: String) -> u16 {
        let wire = self.get_wire(wire_name).clone();

        self.charge(&wire.source, 0)
    }

    fn reset_cache(&self) {
        unsafe {
            (*self.value_cache.get()).clear()
        }
    }

    fn get_cache_value(&self, name: &str) -> Option<&u16> {
        unsafe {
            (*self.value_cache.get()).get(name)
        }
    }

    fn cache_value(&self, name: &str, value: u16) {
        unsafe {
            (*self.value_cache.get()).insert(name.to_string(), value);
        }
    }

    fn charge(&self, source: &Source, indent: usize) -> u16 {
        let indent_string = String::from_utf8(vec![b' '; indent]).unwrap();
        println!("{}Resolving charge for source {:?}", indent_string, source);

        match *source {
            Source::Charge(charge) => charge,
            Source::Wire(ref wire_name) => {
                println!("{}Resolving charge for wire {}", indent_string, wire_name);

                match self.get_cache_value(wire_name) {
                    Some(cached) => {
                        println!("{}Got cached value {} for wire {}", indent_string, cached, wire_name);
                        *cached
                    },
                    None => {
                        let wire = self.get_wire(wire_name.to_string());
                        let computed = self.charge(&wire.source, indent + 1);
                        self.cache_value(wire_name, computed);
                        println!("{}Storing cache value {} for wire {}", indent_string, computed, wire_name);

                        computed
                    }
                }
            },
            Source::Not(ref source) => !self.charge(source, indent + 1),
            Source::And(ref source1, ref source2) => self.charge(source1, indent + 1) & self.charge(source2, indent + 1),
            Source::Or(ref source1, ref source2) => self.charge(source1, indent + 1) | self.charge(source2, indent + 1),
            Source::Xor(ref source1, ref source2) => self.charge(source1, indent + 1) ^ self.charge(source2, indent + 1),
            Source::Rshift(ref source1, ref source2) => self.charge(source1, indent + 1) >> self.charge(source2, indent + 1),
            Source::Lshift(ref source1, ref source2) => self.charge(source1, indent + 1) << self.charge(source2, indent + 1)
        }
    }

    fn add_wire(&mut self, target_wire: &str, source: Source) {
        self.wires.insert(
            target_wire.to_string(),
            Wire {
                name: String::from(target_wire),
                source: source
            }
        );
    }
}



fn get_source(source: &str) -> Source {
    let charge: Option<u16> = source.trim().parse().ok();
    if charge.is_some() {
        return Source::Charge(charge.unwrap());
    }

    let wire_pattern = Regex::new(r"^([a-z]+)$").unwrap();
    if wire_pattern.is_match(source) {
        return Source::Wire(Box::new(source.to_string()));
    }

    let not_pattern = Regex::new(r"NOT ([0-9a-z]+)").unwrap();
    for cap in not_pattern.captures_iter(source) {
        return Source::Not(
            Box::new(get_source(cap.at(1).unwrap()))
        )
    }

    let op_pattern = Regex::new(r"([0-9a-z]+) (AND|OR|XOR|RSHIFT|LSHIFT) ([0-9a-z]+)").unwrap();

    for cap in op_pattern.captures_iter(source) {
        let left = Box::new(get_source(cap.at(1).unwrap()));
        let right = Box::new(get_source(cap.at(3).unwrap()));
        
        return match cap.at(2).unwrap() {
            "AND" => Source::And(left, right),
            "OR" => Source::Or(left, right),
            "XOR" => Source::Xor(left, right),
            "RSHIFT" => Source::Rshift(left, right),
            "LSHIFT" => Source::Lshift(left, right),
            _ => {
                println!("WARNING, unrecognized sources: {}", source);
                Source::Charge(0)
            }
        }
    }
    println!("WARNING, unrecognized sources: {}", source);
    Source::Charge(0)
}



fn main() {
    let f = File::open("input").unwrap();

    let file = BufReader::new(&f);

    let mut circuit = Circuit::new();
    
    for line in file.lines() {
        let l = line.unwrap();

        let mut split = l.split(" -> ");
        
        let source = split.next().unwrap();

        let target_wire = split.next().unwrap();
        let source = get_source(source);

        circuit.add_wire(target_wire, source)
    }

    let charge_a = circuit.wire_charge("a".to_string());
    println!("{}: {}", "a", charge_a);

    circuit.reset_cache();
    circuit.cache_value("b", charge_a);

    let charge_a_part_2 = circuit.wire_charge("a".to_string());
    println!("{}: {}", "a", charge_a_part_2);

}

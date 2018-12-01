package main

import (
    "bufio"
    "fmt"
    "log"
    "os"
    "strings"
)

func main() {
    file, err := os.Open("input")
    if err != nil {
        log.Fatal(err)
    }
    defer file.Close()

    count := 0

    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        line := scanner.Text()

        hasDouble := false
        for i := 2; i < len(line); i++ {
            if line[i] == line[i-2] {
                hasDouble = true
                break
            }
        }

        hasTwoDoubles := false
        for i := 1; i < len(line); i++ {
            if (i > 1 && strings.Contains(line[:i-2], line[i-1:i+1])) ||
                strings.Contains(line[i+1:], line[i-1:i+1]) {
                hasTwoDoubles = true
                break
            }
        }

        if hasDouble && hasTwoDoubles {
            count++
        }
    }
 
    fmt.Println(count)
    

    if err := scanner.Err(); err != nil {
        log.Fatal(err)
    }
}
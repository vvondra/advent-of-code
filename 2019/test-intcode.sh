#!/bin/bash
set -ex
rm -f output && touch output
tsc
node 2.js >> output
node 5.js >> output
node 7.js >> output
node 9.js >> output
node 11.js >> output

diff -b output output-snapshot; ec=$?
case $ec in
    0) echo "OK";;
    *) echo "Command exited with non-zero"; exit 1;;
esac

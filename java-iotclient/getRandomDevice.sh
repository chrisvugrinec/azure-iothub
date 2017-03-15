#!/bin/bash
file=result.txt
max=$(cat $file | wc -l)
nr=$(echo $RANDOM % $max + 1 | bc)
echo $(sed "${nr}q;d" $file)

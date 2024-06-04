#!/bin/bash
#SBATCH -n 8 # Number of cores
#SBATCH -N 1 # Ensure that all cores are on one machine
#SBATCH --mem=64GB # memory
#SBATCH --time=0
#SBATCH --nodelist=boston-2-7
#SBATCH --output=doIndex1.log

set echo=1

set index_dir=/ssd/CW22_ind_en00_00_11_v3_kstem

rm -rf $index_dir/* 

set LUCENE="lucene-9.8.0"
set CLASSPATH=".:$LUCENE/*"

java -classpath "$CLASSPATH" IndexTrec \
     -docs /bos/data0/ClueWeb22_B/txt/en/en00/en0000,/bos/data0/ClueWeb22_B/txt/en/en00/en0001,/bos/data0/ClueWeb22_B/txt/en/en00/en0002,/bos/data0/ClueWeb22_B/txt/en/en00/en0003,/bos/data0/ClueWeb22_B/txt/en/en00/en0004,/bos/data0/ClueWeb22_B/txt/en/en00/en0005,/bos/data0/ClueWeb22_B/txt/en/en00/en0006,/bos/data0/ClueWeb22_B/txt/en/en00/en0007,/bos/data0/ClueWeb22_B/txt/en/en00/en0008,/bos/data0/ClueWeb22_B/txt/en/en00/en0009,/bos/data0/ClueWeb22_B/txt/en/en00/en0010,/bos/data0/ClueWeb22_B/txt/en/en00/en0011 \
     -index $index_dir \
     -lowercase true -stop true -stem kstem \
     -storeField title -storeField body \
     -storeField url \
     -format cw22

#!/bin/bash
#SBATCH -n 8 # Number of cores
#SBATCH -N 1 # Ensure that all cores are on one machine
#SBATCH --mem=64GB # memory
#SBATCH --time=0
#SBATCH --nodelist=boston-2-9
#SBATCH --output=doIndex3.log

set echo=1

set index_dir=/ssd/CW22_ind_en00_24_35_v3_kstem

rm -rf $index_dir/* 

set LUCENE="lucene-9.8.0"
set CLASSPATH=".:$LUCENE/*"

java -classpath "$CLASSPATH" IndexTrec \
     -docs /bos/data0/ClueWeb22_B/txt/en/en00/en0024,/bos/data0/ClueWeb22_B/txt/en/en00/en0025,/bos/data0/ClueWeb22_B/txt/en/en00/en0026,/bos/data0/ClueWeb22_B/txt/en/en00/en0027,/bos/data0/ClueWeb22_B/txt/en/en00/en0028,/bos/data0/ClueWeb22_B/txt/en/en00/en0029,/bos/data0/ClueWeb22_B/txt/en/en00/en0030,/bos/data0/ClueWeb22_B/txt/en/en00/en0031,/bos/data0/ClueWeb22_B/txt/en/en00/en0032,/bos/data0/ClueWeb22_B/txt/en/en00/en0033,/bos/data0/ClueWeb22_B/txt/en/en00/en0034,/bos/data0/ClueWeb22_B/txt/en/en00/en0035 \
     -index $index_dir \
     -lowercase true -stop true -stem kstem \
     -storeField title -storeField body \
     -storeField url \
     -format cw22

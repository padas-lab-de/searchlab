#!/bin/bash
#SBATCH -n 8 # Number of cores
#SBATCH -N 1 # Ensure that all cores are on one machine
#SBATCH --mem=64GB # memory
#SBATCH --time=0
#SBATCH --nodelist=boston-2-8
#SBATCH --output=doIndex2.log

set echo=1

set index_dir=/ssd/CW22_ind_en00_12_23_v3_kstem

rm -rf $index_dir/* 

set LUCENE="lucene-9.8.0"
set CLASSPATH=".:$LUCENE/*"

java -classpath "$CLASSPATH" IndexTrec \
     -docs /bos/data0/ClueWeb22_B/txt/en/en00/en0012,/bos/data0/ClueWeb22_B/txt/en/en00/en0013,/bos/data0/ClueWeb22_B/txt/en/en00/en0014,/bos/data0/ClueWeb22_B/txt/en/en00/en0015,/bos/data0/ClueWeb22_B/txt/en/en00/en0016,/bos/data0/ClueWeb22_B/txt/en/en00/en0017,/bos/data0/ClueWeb22_B/txt/en/en00/en0018,/bos/data0/ClueWeb22_B/txt/en/en00/en0019,/bos/data0/ClueWeb22_B/txt/en/en00/en0020,/bos/data0/ClueWeb22_B/txt/en/en00/en0021,/bos/data0/ClueWeb22_B/txt/en/en00/en0022,/bos/data0/ClueWeb22_B/txt/en/en00/en0023 \
     -index $index_dir \
     -lowercase true -stop true -stem kstem \
     -storeField title -storeField body \
     -storeField url \
     -format cw22

#!/bin/bash
#SBATCH -n 8 # Number of cores
#SBATCH -N 1 # Ensure that all cores are on one machine
#SBATCH --mem=64GB # memory
#SBATCH --time=0
#SBATCH --nodelist=boston-2-10
#SBATCH --output=doIndex4.log

set echo=1

set index_dir=/ssd/CW22_ind_en00_36_46_v3_kstem

rm -rf $index_dir/* 

set LUCENE="lucene-9.8.0"
set CLASSPATH=".:$LUCENE/*"

java -classpath "$CLASSPATH" IndexTrec \
     -docs /bos/data0/ClueWeb22_B/txt/en/en00/en0036,/bos/data0/ClueWeb22_B/txt/en/en00/en0037,/bos/data0/ClueWeb22_B/txt/en/en00/en0038,/bos/data0/ClueWeb22_B/txt/en/en00/en0039,/bos/data0/ClueWeb22_B/txt/en/en00/en0040,/bos/data0/ClueWeb22_B/txt/en/en00/en0041,/bos/data0/ClueWeb22_B/txt/en/en00/en0042,/bos/data0/ClueWeb22_B/txt/en/en00/en0043,/bos/data0/ClueWeb22_B/txt/en/en00/en0044,/bos/data0/ClueWeb22_B/txt/en/en00/en0045,/bos/data0/ClueWeb22_B/txt/en/en00/en0046 \
     -index $index_dir \
     -lowercase true -stop true -stem kstem \
     -storeField title -storeField body \
     -storeField url \
     -format cw22

MASTER="120ceefd08b" #MASTER contains the needed files for running benchmarks, not necessarly master branch
COMMITS="391b6440 dabd406b 53aebd0 8a4ff77eb1"
RESULTS_DIR="results"

RUNNER=run-benchmark.sh
MAIN=src/main/scala/regolic/Main.scala

if [ ! -d $RESULTS_DIR ]; then mkdir $RESULTS_DIR; fi
for commit in $COMMITS; do

  git checkout $commit
  git checkout $MASTER $RUNNER
  git checkout $MASTER $MAIN
  sbt compile
  ./$RUNNER > $RESULTS_DIR/$commit
  git reset HEAD $RUNNER
  git checkout $RUNNER
  git reset HEAD $MAIN
  git checkout $MAIN

done

git checkout master

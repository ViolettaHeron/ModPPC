package org.violetteheron.modppc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.Date;

public class Entrypoint {

    public static void main(String[] args) {

        int[] card = new int[]{8, 9, 10, 11, 12, 13, 14};
        Solver[] allSolversDistances = new Solver[card.length];
        long[] durations = new long[card.length];

        for (int i = 0; i < card.length; i++) {
            int N = card[i];

            Model modelDistances = allDIff2(N);
            long timeNow = new Date().getTime();

            Solver solver = modelDistances.getSolver();

            // solver.setSearch(Search.greedySearch(Search.defaultSearch(modelDistances)));

            solver.findAllSolutions();

            // solver.printStatistics();
            durations[i] = new Date().getTime() - timeNow;
            System.out.println(String.format("%d;%d;%d;%d", N, solver.getSolutionCount(), solver.getBackTrackCount(), durations[i]));
            allSolversDistances[i] = solver;


            //  Model modelDifference = allDIff2(N);
        }


        for (int i = 0; i < allSolversDistances.length; i++) {
            Solver s = allSolversDistances[i];
            System.out.println(String.format("%d;%d;%d;%d", card[i], s.getSolutionCount(), s.getBackTrackCount(), durations[i]));
        }
    }

    private static Model allDIff2(int N) {
        Model model = new Model("all-interval series of size " + N);

        IntVar[] solutions = model.intVarArray("s", N, 0, N - 1, false);

        model.getSolver().setSearch(Search.randomSearch(solutions, System.currentTimeMillis()));
        IntVar[] distances = model.intVarArray("V", N - 1, 1, N - 1, false);


        Tuples tuples = new Tuples();

        // 1. adding our variables to our tuples
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    if (k == Math.abs(i - j) && i != j) {
                        tuples.add(i, j, k);
                    }
                }
            }
        }

        // 2. adding your constraints
        for (int i = 0; i < N - 1; i++) {
            model.table(new IntVar[]{solutions[i], solutions[i + 1], distances[i]}, tuples).post();
        }
        model.allDifferent(solutions).post();
        model.allDifferent(distances).post();

        return model;
    }

    private static Model allDiff1(int N) {
        Model model = new Model("all-interval series of size " + N);
// 1.a declare the variables
        IntVar[] solutions = model.intVarArray("s", N, 0, N - 1, false);
        IntVar[] distances = model.intVarArray("V", N - 1, 1, N - 1, false);
// 1.b post the constraints
        for (int i = 0; i < N - 1; i++) {
            model.distance(solutions[i + 1], solutions[i], "=", distances[i]).post();
        }
        model.allDifferent(solutions).post();
        model.allDifferent(distances).post();

        return model;
    }


}

package Benchmark;
import org.apache.commons.lang3.time.StopWatch;
import org.jgrapht.Graph;
import org.jgrapht.alg.interval.LexBreadthFirstSearch;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.LBFSIteratorOld;
import org.jgrapht.util.SupplierUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class Main {
    static int seed = 0;
    public static void main (String[] args){
//        double e = 200;
//        for (int i = 1; i < 21; i+= 1) {
//            BenchmarkResult result = RunBenchmark(i * 200, 0.1);
//            System.out.println(result.getProblemSize() + "\t" + result.getMilliseconds() + "\t" + result.getNumberOfClasses() + "\t" + result.getNumberOfNonSimplicialClasses());
//        }
        System.out.println("Size \t Old \t LBFS \t LBFS+ \t LBFS*");
        for (int i = 1; i < 2049; i += 100) {
            int limit = 100;
            int time1 = 0, time2 = 0, time3 = 0, time4 = 0;
            for (int j = 0; j < limit; j++) {
                BenchmarkResult result = RunBenchmark(i, 0.1);
                time1 += result.milliseconds1;
                time2 += result.milliseconds2;
                time3 += result.milliseconds3;
                time4 += result.milliseconds4;
            }
            time1 /= limit;
            time2 /= limit;
            time3 /= limit;
            time4 /= limit;
            System.out.println(i + "\t" + time1 + "\t" + time2 + "\t" + time3 + "\t" + time4);
        }
    }

    private static BenchmarkResult RunBenchmark(int size, double edgeProbability) {
        GnpRandomGraphGenerator<Integer, DefaultEdge> generator;
        Graph<Integer, DefaultEdge> graph;
        generator = new GnpRandomGraphGenerator<>(
                size, edgeProbability, seed++);

        graph = new SimpleGraph<>(
                SupplierUtil.createIntegerSupplier(), SupplierUtil.DEFAULT_EDGE_SUPPLIER, false);

        generator.generateGraph(graph);
        List<Integer> list = new ArrayList<>(size);
        System.gc();

        StopWatch watch = new StopWatch();
        watch.start();
        new LBFSIteratorOld<>(graph).forEachRemaining(list::add);
        watch.stop();

        list.clear();
        list = null;
        System.gc();

        StopWatch watch2 = new StopWatch();
        watch2.start();
        HashMap<Integer, Integer> map = LexBreadthFirstSearch.lexBreadthFirstSearch(graph);
        watch2.stop();

        System.gc();

        StopWatch watch3 = new StopWatch();
        watch3.start();
        HashMap<Integer, Integer> map2 = LexBreadthFirstSearch.lexBreadthFirstSearchPlus(graph, map);
        watch3.stop();

        System.gc();

        StopWatch watch4 = new StopWatch();
        watch4.start();
        LexBreadthFirstSearch.lexBreadthFirstSearchStar(graph, map, map2);
        watch4.stop();

        return new BenchmarkResult((int)watch.getTime(), (int) watch2.getTime(), (int) watch3.getTime(), (int) watch4.getTime(), size);
    }


    private static class BenchmarkResult {
        private int milliseconds1;
        private int milliseconds2;
        private int milliseconds3;
        private int milliseconds4;
        private int problemSize;

        private BenchmarkResult(int milliseconds1, int milliseconds2, int milliseconds3, int milliseconds4, int problemSize) {
            this.milliseconds1 = milliseconds1;
            this.milliseconds2 = milliseconds2;
            this.milliseconds3 = milliseconds3;
            this.milliseconds4 = milliseconds4;

            this.problemSize = problemSize;
        }

        public int getProblemSize() {
            return problemSize;
        }
    }
}


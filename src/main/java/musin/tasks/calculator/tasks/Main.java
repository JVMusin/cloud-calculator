package musin.tasks.calculator.tasks;

import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class Main {

  public void run() {
    new TaskG().solve();
  }

  static class TaskG {
    static int gcd(int a, int b) {
      if (b == 0) return a;
      return gcd(b, a % b);
    }

    static long cn3(long n) {
      return n * (n - 1) * (n - 2) / 6;
    }

    @SneakyThrows
    public void solve() {
      System.out.println("FJP Parallelism is " + ForkJoinPool.getCommonPoolParallelism());
      long startTime = System.currentTimeMillis();
      System.out.println("STARTED");
      List<Solver> solvers = new ArrayList<>();
      for (int n = 1; n <= 100; n++) {
        for (int m = n; m <= 100; m++) {
          if (n * m <= 1024) {
            solvers.add(new Solver(n, m));
          }
        }
      }
      List<Callable<Solver>> tasks = solvers.stream()
          .map(s -> (Callable<Solver>) () -> {
            if (s.res == -1) {
              long res = s.solve();
              System.out.println(ForkJoinPool.commonPool());
              System.out.printf("Task for n=%d m=%d solved with result=%d\n", s.n, s.m, res);
            }
            return s;
          })
          .collect(Collectors.toList());
      ForkJoinPool.commonPool().invokeAll(tasks);
      long failed = solvers.stream().filter(s -> s.res == -1).count();
      System.out.println("failed " + failed + " tasks");
      for (var s : tasks) s.call();
      System.out.println("All tasks ready");
      System.out.printf("Time elapsed: %.3f\n", (System.currentTimeMillis() - startTime) / 1000.0);
      for (Solver solver : solvers) {
        System.out.printf("save(mp(%d,%d), %d);\n", solver.n, solver.m, solver.res);
      }
      System.out.println("DONE");
      Thread.sleep(1000);
    }

    static class Solver {
      int n;
      int m;
      long res = -1;

      public Solver(int n, int m) {
        this.n = n;
        this.m = m;
      }

      long solve() {
        Map<Line, Set<Integer>> cnt = new TreeMap<>();
        int nm = n * m;
        for (int i = 0; i < nm; i++) {
          for (int j = i + 1; j < nm; j++) {
            Line line = Line.build(new Point(i / m, i % m), new Point(j / m, j % m));
            if (!cnt.containsKey(line)) cnt.put(line, new HashSet<>());
            cnt.get(line).add(i);
            cnt.get(line).add(j);
          }
        }
        res = cn3(nm);
        for (Set<Integer> c : cnt.values()) res -= cn3(c.size());
        return res;
      }
    }

    static class Point {
      int x;
      int y;

      public Point(int x, int y) {
        this.x = x;
        this.y = y;
      }
    }

    static class Line implements Comparable<Line> {
      int a;
      int b;
      int c;

      public Line(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
      }

      static Line build(int a, int b, int c) {
        if (a < 0 || (a == 0 && b < 0) || (a == 0 && b == 0 && c < 0)) {
          a = -a;
          b = -b;
          c = -c;
        }
        int g = gcd(Math.abs(a), Math.abs(b));
        a /= g;
        b /= g;
        c /= g;
        return new Line(a, b, c);
      }

      static Line build(Point p, Point q) {
        int a = p.y - q.y;
        int b = q.x - p.x;
        int c = -(a * p.x + b * p.y);
        return build(a, b, c);
      }

      public int compareTo(Line o) {
        int comp = Integer.compare(a, o.a);
        if (comp == 0) comp = Integer.compare(b, o.b);
        if (comp == 0) comp = Integer.compare(c, o.c);
        return comp;
      }
    }
  }
}

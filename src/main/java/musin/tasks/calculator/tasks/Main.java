package musin.tasks.calculator.tasks;

import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 *
 * @author Rustam Musin (t.me/JVMusin)
 */
@Component
public class Main implements ApplicationListener<ApplicationReadyEvent> {
  public static void main(String[] args) {
    InputStream inputStream = System.in;
    OutputStream outputStream = System.out;
    InputReader in = new InputReader(inputStream);
    OutputWriter out = new OutputWriter(outputStream);
    TaskG solver = new TaskG();
    solver.solve(1, in, out);
    out.close();
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    new TaskG().solve(-1, null, null);
  }

  static class TaskG {
    @SneakyThrows
    public void solve(int testNumber, InputReader in, OutputWriter out) {
      System.out.println("FJP Parallelism is " + ForkJoinPool.commonPool());
      long startTime = System.currentTimeMillis();
      System.out.println("STARTED");
      List<Solver> solvers = new ArrayList<>();
      for (int n = 1; n <= 10; n++) {
        for (int m = n; m <= 20; m++) {
          if (n * m <= 1024) {
            solvers.add(new Solver(n, m));
          }
        }
      }
      List<Callable<Solver>> tasks = solvers.stream()
          .map(s -> (Callable<Solver>) () -> {
            long res = s.solve();
            System.out.printf("Task for n=%d m=%d solved with result=%d\n", s.n, s.m, res);
            return s;
          })
          .collect(Collectors.toList());
      List<Future<Solver>> futures = ForkJoinPool.commonPool().invokeAll(tasks);
      System.out.println("All tasks submitted");
      for (Future<Solver> future : futures) future.get();
      System.out.println("All tasks ready");
      System.out.printf("Time elapsed: %.3f\n", (System.currentTimeMillis() - startTime) / 1000.0);
      for (Future<Solver> future : futures) {
        Solver s = future.get();
        System.out.printf("save(mp(%d,%d), %d);\n", s.n, s.m, s.res);
      }
      System.out.println("DONE");
      Thread.sleep(1000);
    }

    static int gcd(int a, int b) {
      if (b == 0) return a;
      return gcd(b, a % b);
    }

    static long cn3(long n) {
      return n * (n - 1) * (n - 2) / 6;
    }

    static class Solver {
      int n;
      int m;
      long res;
      Point[] a;

      public Solver(int n, int m) {
        this.n = n;
        this.m = m;
      }

      long solve() {
        a = new Point[n * m];
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < m; j++) {
            a[i * m + j] = new Point(i, j);
          }
        }
        Map<Line, Set<Integer>> cnt = new TreeMap<>();
        for (int i = 0; i < a.length; i++) {
          for (int j = i + 1; j < a.length; j++) {
            Line line = Line.build(a[i], a[j]);
            if (!cnt.containsKey(line)) cnt.put(line, new HashSet<>());
            cnt.get(line).add(i);
            cnt.get(line).add(j);
          }
        }
        res = cn3(a.length);
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

  static class OutputWriter {
    private final PrintWriter writer;

    public OutputWriter(OutputStream outputStream) {
      writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
    }

    public OutputWriter(Writer writer) {
      this.writer = new PrintWriter(writer);
    }

    public void close() {
      writer.close();
    }

  }

  static class InputReader {
    private InputStream stream;

    public InputReader(InputStream stream) {
      this.stream = stream;
    }

  }
}


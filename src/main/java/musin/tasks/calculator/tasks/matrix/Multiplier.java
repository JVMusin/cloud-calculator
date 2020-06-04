package musin.tasks.calculator.tasks.matrix;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

public class Multiplier implements Runnable {
  private final MatrixGenerator generator;
  private final List<MatrixPair> matricesToMultiply;
  private final int threadCount;

  public Multiplier(int matrixSize, int matrixValueBound, int multiplicationCount, int threadCount, int randomSeed) {
    generator = new MatrixGenerator(randomSeed);
    matricesToMultiply = IntStream.range(0, multiplicationCount)
        .mapToObj(i -> new MatrixPair(
            generator.generate(matrixSize, matrixValueBound),
            generator.generate(matrixSize, matrixValueBound)))
        .collect(toList());
    this.threadCount = threadCount;
  }

  @Override
  public void run() {
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    List<CompletableFuture<Matrix>> results = matricesToMultiply.stream()
        .map(p -> supplyAsync(p::multiply, executor))
        .collect(toList());

    allOf(results.toArray(CompletableFuture[]::new)).join();

    executor.shutdown();
  }

  private static class MatrixPair {
    final Matrix a, b;

    MatrixPair(Matrix a, Matrix b) {
      this.a = a;
      this.b = b;
    }

    Matrix multiply() {
      return a.multiply(b);
    }
  }
}

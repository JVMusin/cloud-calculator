package musin.tasks.calculator.tasks.matrix;

public class Tester implements Runnable {
  private static final int TEST_COUNT = 10;

  private static final int MATRIX_SIZE = 200;
  private static final int MATRIX_VALUE_BOUND = 1000;
  private static final int MULTIPLICATION_COUNT = 100;
  private static final int BASE_SEED = 239;

  private final int threadCount;

  public Tester(int threadCount) {
    this.threadCount = threadCount;
  }

  @Override
  public void run() {
    long totalTime = 0;
    for (int i = 0; i < TEST_COUNT; i++) {
      Multiplier multiplier = new Multiplier(MATRIX_SIZE, MATRIX_VALUE_BOUND, MULTIPLICATION_COUNT, threadCount, BASE_SEED * (i + 1));
      long time = measure(multiplier);
      System.out.printf("Iteration #%d: %.3fs\n", i, time / 1000.0);
      totalTime += time;
    }
    System.out.printf("Total time: %.3fs\n", totalTime / 1000.0);
    System.out.printf("Average time: %.3fs\n", totalTime / 1000.0 / TEST_COUNT);
  }

  private long measure(Runnable task) {
    long start = System.currentTimeMillis();
    task.run();
    return System.currentTimeMillis() - start;
  }
}
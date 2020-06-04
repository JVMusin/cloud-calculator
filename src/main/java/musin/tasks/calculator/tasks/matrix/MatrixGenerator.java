package musin.tasks.calculator.tasks.matrix;

import java.util.Random;

public class MatrixGenerator {
  private final Random rnd;

  public MatrixGenerator(int seed) {
    rnd = new Random(seed);
  }

  public Matrix generate(int size, int valueBound) {
    int[][] a = new int[size][size];
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++)
        a[i][j] = rnd.nextInt(valueBound);
    return new Matrix(a);
  }
}

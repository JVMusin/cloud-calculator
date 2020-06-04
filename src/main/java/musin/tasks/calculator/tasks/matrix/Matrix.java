package musin.tasks.calculator.tasks.matrix;

public class Matrix {
  private final int n;
  private final int[][] a;

  public Matrix(int[][] a) {
    this.a = a;
    n = a.length;
  }

  public Matrix multiply(Matrix m) {
    int[][] res = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        int r = 0;
        for (int k = 0; k < n; k++) r += a[i][k] * m.a[k][j];
        res[i][j] = r;
      }
    }
    return new Matrix(res);
  }
}

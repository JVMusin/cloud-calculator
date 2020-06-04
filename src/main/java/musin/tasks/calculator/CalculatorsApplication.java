package musin.tasks.calculator;

import musin.tasks.calculator.tasks.matrix.Tester;

public class CalculatorsApplication {

  public static void main(String[] args) {
    int threadCount = args.length == 0 ? 1 : Integer.parseInt(args[0]);
    System.out.println("THREAD COUNT IS " + threadCount);
    new Tester(threadCount).run();
  }
}

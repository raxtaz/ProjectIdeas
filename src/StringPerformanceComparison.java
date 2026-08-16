public class StringPerformanceComparison {

	private static final int ITERATIONS = 100_000;

	public static void main(String[] args) {
		System.out.println("Benchmarking loop with " + ITERATIONS + " iterations:\n");

		// 1. String Concatenation (+)
		long startTime = System.currentTimeMillis();
		String str = "";
		for (int i = 0; i < ITERATIONS; i++) {
			str += "a";
		}
		long stringDuration = System.currentTimeMillis() - startTime;
		System.out.println("1. String (+)          : " + stringDuration + " ms");

		// 2. StringBuffer (Mutable, Thread-Safe / Synchronized)
		startTime = System.currentTimeMillis();
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.repeat("a", ITERATIONS);
		long stringBufferDuration = System.currentTimeMillis() - startTime;
		System.out.println("2. StringBuffer        : " + stringBufferDuration + " ms");

		// 3. StringBuilder (Mutable, Non-Synchronized / Fast)
		startTime = System.currentTimeMillis();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.repeat("a", ITERATIONS);
		long stringBuilderDuration = System.currentTimeMillis() - startTime;
		System.out.println("3. StringBuilder       : " + stringBuilderDuration + " ms");
	}
}
import java.util.ArrayList;
import java.util.List;

public class ConfigData_Of_GUI {
	public enum Mode { SEQ, PAR }

	private Mode mode;
	private long totalPoints;
	private int numTasks;
	private int numThreads;
	private int runs;

	public static class RunResult {
		public final int runIndex;
		public final double pi;
		public final double timeMs;
		public final double errorPct;

		public RunResult(int runIndex, double pi, double timeMs, double errorPct) {
			this.runIndex = runIndex;
			this.pi = pi;
			this.timeMs = timeMs;
			this.errorPct = errorPct;
		}
	}

	private final List<RunResult> results = new ArrayList<>();

	public synchronized void clearResults() { results.clear(); }
	public synchronized void addResult(RunResult r) { results.add(r); }
	public synchronized List<RunResult> getResults() { return new ArrayList<>(results); }

	// getters/setters
	public Mode getMode() { return mode; }
	public void setMode(Mode mode) { this.mode = mode; }
	public long getTotalPoints() { return totalPoints; }
	public void setTotalPoints(long totalPoints) { this.totalPoints = totalPoints; }
	public int getNumTasks() { return numTasks; }
	public void setNumTasks(int numTasks) { this.numTasks = numTasks; }
	public int getNumThreads() { return numThreads; }
	public void setNumThreads(int numThreads) { this.numThreads = numThreads; }
	public int getRuns() { return runs; }
	public void setRuns(int runs) { this.runs = runs; }
}


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class MainWindow extends JFrame {

    // --- Colors & Theme ---
    private static final Color BG_COLOR = new Color(30, 30, 30); // Dark background
    private static final Color PANEL_COLOR = new Color(45, 45, 45); // Slightly lighter panel
    private static final Color TEXT_COLOR = new Color(230, 230, 230); // Off-white text
    private static final Color ACCENT_COLOR = new Color(0, 173, 181); // Cyan accent (for UI elements)
    private static final Color SECONDARY_ACCENT = new Color(255, 57, 57); // Red accent
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Simulation Colors
    private static final Color POINT_INSIDE = Color.GREEN;
    private static final Color POINT_OUTSIDE = Color.RED;

    // --- Components ---
    private SimulationPanel simulationPanel;
    private ChartPanel chartPanel;
    private ConfigPanel configPanel;

    // --- State ---
    private final List<RunData> runHistory = new ArrayList<>();
    private SwingWorker<?, ?> currentWorker;

    public MainWindow() {
        super("Monte Carlo Simulation - Performance Comparison");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // 1. Sidebar (Configuration)
        configPanel = new ConfigPanel(this::onStartClicked, this::onStopClicked);
        add(configPanel, BorderLayout.WEST);

        // 2. Main Content (Simulation + Chart)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        simulationPanel = new SimulationPanel();
        chartPanel = new ChartPanel();

        centerPanel.add(simulationPanel);
        centerPanel.add(chartPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void onStartClicked() {
        if (currentWorker != null && !currentWorker.isDone())
            return;

        // Reset simulation view
        simulationPanel.clear();

        // Get Inputs
        long n = configPanel.getPoints();
        int tasks = configPanel.getTasks();
        int threads = configPanel.getThreads();
        int runs = configPanel.getRuns();
        boolean isParallel = configPanel.isParallel();

        configPanel.setRunning(true);

        if (isParallel) {
            currentWorker = new ParallelWorker(n, runs, tasks, threads);
        } else {
            currentWorker = new SequentialWorker(n, runs);
        }
        currentWorker.execute();
    }

    private void onStopClicked() {
        if (currentWorker != null) {
            currentWorker.cancel(true);
        }
        configPanel.setRunning(false);
    }

    // --- Data Classes ---
    private static class RunData {
        boolean isParallel;
        int runIndex;
        long n;
        double timeMs;
        double pi;

        public RunData(boolean isParallel, int runIndex, long n, double timeMs, double pi) {
            this.isParallel = isParallel;
            this.runIndex = runIndex;
            this.n = n;
            this.timeMs = timeMs;
            this.pi = pi;
        }
    }

    // --- Sub-components ---

    // 1. Config Panel (Sidebar)
    class ConfigPanel extends JPanel {
        private JTextField nField, tasksField, threadsField, runsField;
        private JRadioButton sequentialBtn, parallelBtn;
        private JButton startBtn, stopBtn;
        private JLabel statusLabel;

        Runnable startAction, stopAction;

        public ConfigPanel(Runnable startAction, Runnable stopAction) {
            this.startAction = startAction;
            this.stopAction = stopAction;

            setLayout(new GridBagLayout());
            setBackground(PANEL_COLOR);
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setPreferredSize(new Dimension(300, 0));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            // Title
            JLabel title = new JLabel("Configuration");
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(ACCENT_COLOR);
            add(title, gbc);

            gbc.gridy++;
            add(createSeparator(), gbc);

            // Mode Selection
            gbc.gridy++;
            add(createLabel("Execution Mode:"), gbc);

            gbc.gridy++;
            JPanel radioPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            radioPanel.setBackground(PANEL_COLOR);

            sequentialBtn = createRadio("Sequential");
            parallelBtn = createRadio("Parallel");
            parallelBtn.setSelected(true);

            ButtonGroup group = new ButtonGroup();
            group.add(sequentialBtn);
            group.add(parallelBtn);

            radioPanel.add(sequentialBtn);
            radioPanel.add(parallelBtn);
            add(radioPanel, gbc);

            // Inputs
            gbc.gridy++;
            add(createLabel("N (Total Points):"), gbc);
            gbc.gridy++;
            nField = createTextField("100000");
            add(nField, gbc);

            // Task & Thread Inputs (Only for Parallel)
            gbc.gridy++;
            add(createLabel("Number of Tasks:"), gbc);
            gbc.gridy++;
            tasksField = createTextField("4");
            add(tasksField, gbc);

            gbc.gridy++;
            add(createLabel("Number of Threads:"), gbc);
            gbc.gridy++;
            threadsField = createTextField(String.valueOf(Runtime.getRuntime().availableProcessors()));
            add(threadsField, gbc);

            gbc.gridy++;
            add(createLabel("Number of Runs:"), gbc);
            gbc.gridy++;
            runsField = createTextField("1");
            add(runsField, gbc);

            // Buttons
            gbc.gridy++;
            gbc.insets = new Insets(30, 0, 10, 0);
            startBtn = createButton("START SIMULATION", ACCENT_COLOR);
            startBtn.addActionListener(e -> startAction.run());
            add(startBtn, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(10, 0, 10, 0);
            stopBtn = createButton("STOP", new Color(200, 50, 50));
            stopBtn.addActionListener(e -> stopAction.run());
            stopBtn.setEnabled(false);
            add(stopBtn, gbc);

            // Status
            gbc.gridy++;
            gbc.weighty = 1.0; // push to top
            gbc.anchor = GridBagConstraints.NORTH;
            statusLabel = new JLabel("Ready");
            statusLabel.setForeground(Color.GRAY);
            add(statusLabel, gbc);

            // Logic to enable/disable specific fields
            parallelBtn.addActionListener(e -> updateFieldsState());
            sequentialBtn.addActionListener(e -> updateFieldsState());

            // Initial state
            updateFieldsState();
        }

        private void updateFieldsState() {
            boolean isPar = parallelBtn.isSelected();
            boolean notRunning = startBtn.isEnabled();
            tasksField.setEnabled(isPar && notRunning);
            threadsField.setEnabled(isPar && notRunning);
        }

        public long getPoints() {
            return parseLong(nField.getText(), 10000);
        }

        public int getTasks() {
            return parseInt(tasksField.getText(), 4);
        }

        public int getThreads() {
            return parseInt(threadsField.getText(), 4);
        }

        public int getRuns() {
            return parseInt(runsField.getText(), 1);
        }

        public boolean isParallel() {
            return parallelBtn.isSelected();
        }

        public void setRunning(boolean running) {
            startBtn.setEnabled(!running);
            stopBtn.setEnabled(running);

            nField.setEnabled(!running);
            runsField.setEnabled(!running);
            sequentialBtn.setEnabled(!running);
            parallelBtn.setEnabled(!running);

            // Re-evaluate parallel fields based on mode
            updateFieldsState();
            if (running) {
                tasksField.setEnabled(false);
                threadsField.setEnabled(false);
            }

            statusLabel.setText(running ? "Running..." : "Ready");
            statusLabel.setForeground(running ? ACCENT_COLOR : Color.GRAY);
        }

        private JRadioButton createRadio(String text) {
            JRadioButton rb = new JRadioButton(text);
            rb.setBackground(PANEL_COLOR);
            rb.setForeground(TEXT_COLOR);
            rb.setFont(MAIN_FONT);
            rb.setFocusPainted(false);
            return rb;
        }

        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setForeground(TEXT_COLOR);
            lbl.setFont(MAIN_FONT);
            return lbl;
        }

        private JTextField createTextField(String def) {
            JTextField tf = new JTextField(def);
            tf.setBackground(BG_COLOR);
            tf.setForeground(TEXT_COLOR);
            tf.setCaretColor(ACCENT_COLOR);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.GRAY, 1),
                    new EmptyBorder(5, 5, 5, 5)));
            tf.setFont(MAIN_FONT);
            return tf;
        }

        private JButton createButton(String text, Color bg) {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed())
                        g.setColor(bg.darker());
                    else if (getModel().isRollover())
                        g.setColor(bg.brighter());
                    else
                        g.setColor(isEnabled() ? bg : bg.darker().darker());
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g);
                }
            };
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setPreferredSize(new Dimension(0, 40));
            return btn;
        }

        private JSeparator createSeparator() {
            JSeparator s = new JSeparator();
            s.setForeground(Color.GRAY);
            s.setBackground(PANEL_COLOR);
            return s;
        }

        private int parseInt(String s, int def) {
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                return def;
            }
        }

        private long parseLong(String s, long def) {
            try {
                return Long.parseLong(s.trim());
            } catch (Exception e) {
                return def;
            }
        }
    }

    // 2. Simulation Panel (Visualizer)
    class SimulationPanel extends JPanel {
        private final List<PointColor> points = new CopyOnWriteArrayList<>();
        private long totalPoints = 0;
        private long insidePoints = 0;
        private double piEstimate = 0;

        public SimulationPanel() {
            setBackground(BG_COLOR);
            setBorder(new LineBorder(PANEL_COLOR, 2));
        }

        public void clear() {
            points.clear();
            totalPoints = 0;
            insidePoints = 0;
            piEstimate = 0;
            repaint();
        }

        public void addPoints(List<PointColor> newPoints) {
            points.addAll(newPoints);
            // Limit points to avoid OOM for layout demo if massive
            // Keep more points for better visualization if machine handles it
            if (points.size() > 50000) {
                points.subList(0, points.size() - 50000).clear();
            }
            repaint();
        }

        public void updateStats(long total, long inside) {
            this.totalPoints = total;
            this.insidePoints = inside;
            this.piEstimate = (total == 0) ? 0 : 4.0 * inside / total;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int s = Math.min(w, h) - 40;
            int x = (w - s) / 2;
            int y = (h - s) / 2;

            // Draw Box
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, s, s);

            // Draw Circle Outline
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawOval(x, y, s, s);

            // Draw Points
            for (PointColor p : points) {
                int px = x + (int) (p.x * s);
                int py = y + (int) (p.y * s);
                g2.setColor(p.color);
                g2.fillRect(px, py, 2, 2);
            }

            // Overlay Stats (HUD style)
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(10, 10, 200, 80, 10, 10);

            g2.setColor(ACCENT_COLOR);
            g2.setFont(HEADER_FONT);
            g2.drawString(String.format("Pi: %.5f", piEstimate), 20, 35);

            g2.setColor(Color.WHITE);
            g2.setFont(MAIN_FONT);
            g2.drawString("Total: " + totalPoints, 20, 60);
            g2.drawString("Inside: " + insidePoints, 20, 80);
        }
    }

    static class PointColor {
        double x, y;
        Color color;

        PointColor(double x, double y, Color c) {
            this.x = x;
            this.y = y;
            this.color = c;
        }
    }

    // 3. Chart Panel (Comparison)
    class ChartPanel extends JPanel {
        public ChartPanel() {
            setBackground(PANEL_COLOR);
            setBorder(new LineBorder(BG_COLOR, 2));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 40;

            // Draw Axes
            g2.setColor(Color.GRAY);
            g2.drawLine(padding, h - padding, w - padding, h - padding); // X
            g2.drawLine(padding, h - padding, padding, padding); // Y

            if (runHistory.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("No data yet. Run simulations to compare.", w / 2 - 100, h / 2);
                return;
            }

            // Find Max Time for Scaling
            double maxTime = runHistory.stream().mapToDouble(r -> r.timeMs).max().orElse(1.0);
            if (maxTime == 0)
                maxTime = 1;

            // Plot Data
            Path2D seqPath = new Path2D.Double();
            Path2D parPath = new Path2D.Double();

            // Separate data
            List<RunData> seqRuns = new ArrayList<>();
            List<RunData> parRuns = new ArrayList<>();
            for (RunData r : runHistory) {
                if (r.isParallel)
                    parRuns.add(r);
                else
                    seqRuns.add(r);
            }

            int totalRunsCount = Math.max(seqRuns.size(), parRuns.size());
            // X-step
            double xStep = (double) (w - 2 * padding) / Math.max(1, totalRunsCount - 1);
            if (totalRunsCount == 1)
                xStep = 0;
            if (totalRunsCount == 0)
                xStep = 0; // Guard

            // Draw Sequential (Red)
            drawSeries(g2, seqRuns, SECONDARY_ACCENT, padding, h, padding, maxTime, xStep);

            // Draw Parallel (Cyan)
            drawSeries(g2, parRuns, ACCENT_COLOR, padding, h, padding, maxTime, xStep);

            // Legend
            g2.setColor(SECONDARY_ACCENT);
            g2.fillRect(w - 150, 20, 10, 10);
            g2.setColor(TEXT_COLOR);
            g2.drawString("Sequential", w - 135, 30);

            g2.setColor(ACCENT_COLOR);
            g2.fillRect(w - 150, 40, 10, 10);
            g2.setColor(TEXT_COLOR);
            g2.drawString("Parallel", w - 135, 50);
        }

        private void drawSeries(Graphics2D g2, List<RunData> data, Color c, int pad, int h, int padY, double maxTime,
                double xStep) {
            if (data.isEmpty())
                return;

            g2.setColor(c);
            g2.setStroke(new BasicStroke(2f));

            Path2D path = new Path2D.Double();
            for (int i = 0; i < data.size(); i++) {
                RunData r = data.get(i);
                double x = pad + (i * xStep);
                if (data.size() == 1)
                    x = getWidth() / 2.0;

                double normalizedTime = r.timeMs / maxTime;
                double y = (h - padY) - (normalizedTime * (h - 2 * padY));

                if (i == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);

                // Draw dot
                g2.fill(new Ellipse2D.Double(x - 4, y - 4, 8, 8));
                // Draw label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(String.format("%.0f ms", r.timeMs), (int) x, (int) y - 10);
            }
            g2.draw(path);
        }
    }

    // --- Workers ---

    private class SequentialWorker extends SwingWorker<Void, PointColor> {
        private final long n;
        private final int runs;

        public SequentialWorker(long n, int runs) {
            this.n = n;
            this.runs = runs;
        }

        @Override
        protected Void doInBackground() {
            for (int r = 0; r < runs && !isCancelled(); r++) {
                long start = System.nanoTime();
                long inside = 0;

                for (long i = 0; i < n && !isCancelled(); i++) {
                    double x = ThreadLocalRandom.current().nextDouble();
                    double y = ThreadLocalRandom.current().nextDouble();
                    boolean inCircle = Math.pow(x - 0.5, 2) + Math.pow(y - 0.5, 2) <= 0.25;

                    if (inCircle)
                        inside++;

                    // Publish chunks
                    if (i % 2000 == 0) {
                        publish(new PointColor(x, y, inCircle ? POINT_INSIDE : POINT_OUTSIDE));
                        final long currentInside = inside;
                        final long currentTotal = i + 1;
                        SwingUtilities.invokeLater(() -> simulationPanel.updateStats(currentTotal, currentInside));
                    }
                }

                long end = System.nanoTime();
                double timeMs = (end - start) / 1e6;
                double pi = 4.0 * inside / n;

                runHistory.add(new RunData(false, runHistory.size() + 1, n, timeMs, pi));
                SwingUtilities.invokeLater(chartPanel::repaint);
            }
            return null;
        }

        @Override
        protected void process(List<PointColor> chunks) {
            simulationPanel.addPoints(chunks);
        }

        @Override
        protected void done() {
            configPanel.setRunning(false);
        }
    }

    private class ParallelWorker extends SwingWorker<Void, PointColor> {
        private final long n;
        private final int runs;
        private final int numTasks;
        private final int numThreads;

        public ParallelWorker(long n, int runs, int numTasks, int numThreads) {
            this.n = n;
            this.runs = runs;
            this.numTasks = numTasks;
            this.numThreads = numThreads;
        }

        @Override
        protected Void doInBackground() {
            ExecutorService pool = Executors.newFixedThreadPool(numThreads);

            for (int r = 0; r < runs && !isCancelled(); r++) {
                long start = System.nanoTime();
                LongAdder insideRef = new LongAdder();
                AtomicInteger tasksFinished = new AtomicInteger(0);

                long pointsPerTask = n / numTasks;

                for (int t = 0; t < numTasks; t++) {
                    long taskPoints = (t == numTasks - 1) ? (n - (pointsPerTask * (numTasks - 1))) : pointsPerTask;

                    pool.submit(() -> {
                        ThreadLocalRandom rnd = ThreadLocalRandom.current();
                        List<PointColor> batch = new ArrayList<>(1000);

                        for (long i = 0; i < taskPoints && !Thread.currentThread().isInterrupted(); i++) {
                            double x = rnd.nextDouble();
                            double y = rnd.nextDouble();
                            boolean inCircle = Math.pow(x - 0.5, 2) + Math.pow(y - 0.5, 2) <= 0.25;
                            if (inCircle)
                                insideRef.increment();

                            // Batch publishing
                            // Since we are multiple threads, we don't want to overwhelm EDT.
                            // Only publish occasionally.
                            if (i % 2000 == 0) {
                                publish(new PointColor(x, y, inCircle ? POINT_INSIDE : POINT_OUTSIDE));
                            }
                        }
                        tasksFinished.incrementAndGet();
                    });
                }

                // Wait loop
                while (tasksFinished.get() < numTasks && !isCancelled()) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                long end = System.nanoTime();
                double timeMs = (end - start) / 1e6;
                double pi = 4.0 * insideRef.sum() / n;

                runHistory.add(new RunData(true, runHistory.size() + 1, n, timeMs, pi));
                SwingUtilities.invokeLater(() -> {
                    simulationPanel.updateStats(n, insideRef.sum());
                    chartPanel.repaint();
                });
            }
            pool.shutdownNow();
            return null;
        }

        @Override
        protected void process(List<PointColor> chunks) {
            simulationPanel.addPoints(chunks);
        }

        @Override
        protected void done() {
            configPanel.setRunning(false);
        }
    }
}

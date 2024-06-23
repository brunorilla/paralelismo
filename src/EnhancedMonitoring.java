import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnhancedMonitoring {
    private final ScheduledExecutorService monitorExecutor = Executors.newScheduledThreadPool(1);
    private final com.sun.management.OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    public void startMonitoring() {
        monitorExecutor.scheduleAtFixedRate(this::logPerformanceMetrics, 0, 1, TimeUnit.SECONDS);
    }

    public void stopMonitoring() {
        monitorExecutor.shutdownNow();
    }

    private void logPerformanceMetrics() {
        double systemCpuLoad = osBean.getCpuLoad();
        double processCpuLoad = osBean.getProcessCpuLoad();
        long processCpuTime = threadBean.getCurrentThreadCpuTime();
        long processUserTime = threadBean.getCurrentThreadUserTime();

        // Kernel time can be calculated as cpuTime - userTime if needed
        long kernelTime = processCpuTime - processUserTime;

        System.out.println("System CPU Load: " + (systemCpuLoad * 100) + "%");
        System.out.println("Process CPU Load: " + (processCpuLoad * 100) + "%");
        System.out.println("Process CPU Time: " + processCpuTime + " ns");
        System.out.println("Process User Time: " + processUserTime + " ns");
        System.out.println("Kernel Time: " + kernelTime + " ns");
    }
}

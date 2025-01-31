package de.mm65.threadload;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    private static final List<Group> GROUPS = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final int coreCount = isWindows() ? availableProcessors / 2 : availableProcessors;
        System.out.println(MessageFormat.format("Runtime reported {0} processor(s) available.", availableProcessors));
        System.out.println(MessageFormat.format("Testing with {0} group(s) of threads.", coreCount));
        System.out.println("Each group of threads is intended to keep just one core busy at 100%.");
        System.out.println("Example: If one group contains 3 threads, then the first thread causes 1/6 of 100% load, ");
        System.out.println("         the second 2/6 and the third 3/6. Together they cause 6/6 of 100% load on one core.");
        System.out.println("         All threads of one group are intended to cause 100% load on one core together.");

        buildGroups(coreCount);

        System.out.println("\nPress <ENTER> to stop the CPU load test with multiple threads.");
        System.in.read();

        terminateGroups();
        waitForGroupsTermination();
    }

    private static boolean isWindows() {
        final String osName = System.getProperty("os.name");
        return osName.toLowerCase().contains("windows");
    }

    private static void buildGroups(final int coreCount) {
        IntStream.range(0, coreCount).forEach(Main::buildGroup);
    }

    private static void buildGroup(final int groupIndex) {
        final int threadCount = groupIndex + 1;
        final String threadNamePrefix = MessageFormat.format("G{0}_T", threadCount);
        final Group group = new Group(threadNamePrefix, threadCount);
        group.startThreads();
        GROUPS.add(group);
    }

    private static void terminateGroups() {
        GROUPS.forEach(Group::terminateThreads);
    }

    private static void waitForGroupsTermination() {
        GROUPS.forEach(Group::waitForThreadTermination);
    }

}
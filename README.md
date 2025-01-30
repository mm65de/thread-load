# Sample Java application with several threads that place different loads on the CPU

This is a sample Java application that detects the count of CPU cores and creates the same amount of thread group.

* The first thread group contains only one thread and this threads causes a load of 100 % on one CPU core.
* The second thread group contains two threads. Both threads together cause a load of 100 % on one CPU core. The load of these threads are in a numerical ratio of 1:2.
* The third thread group contains three threads. All three threads together cause a load of 100 % on one CPU core. The load of these threads are in a numerical ratio of 1:2:3.
* Etc.

The Java source compatibility of this project is set to 1.8.

The application can be started via:

java -jar Thread-Load.jar

# Monitor the thread load with top or htop under Linux

Other than the TaskManager of Windows, where you at most see the count of threads, top and htop allow to show the threads of the Java application and the CPU that each thread causes.

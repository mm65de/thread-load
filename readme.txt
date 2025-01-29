Bugs/TODOs:

- Only two threads in each group take part in the "heavy calculation".
  That means that notify does not notify all waiting threads round robin, but probably the thread used as latest.
  Here a explicit notify is needed.

  So the list of threads of the group and the current index must be passed to the run method.
- Runtime.getRuntime().availableProcessors() apparently returns the virtual cores (12) under Windows,
  but real cores (6) under Docker.
  Perhaps the OS can be detected by some other means...
  And dependent on the OS the count of needed groups can be derived.

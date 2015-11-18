# kill-test
A test about dealing with UNIX kill signals and interrupting threads

Running:
```
java -jar kill-test-1.0.0-SNAPSHOT.jar
```

Ctrl+C:
```
Running 'non-blocking'
Running 'blocking'
^CInterrupting threads
Interrupting 'class org.brutusin.kill.KillTest$2' termination
Interrupting 'class org.brutusin.kill.KillTest$3' termination
Waiting 'non-blocking' termination
Thread 'blocking' interrupted
Checking interrupted
Thread 'non-blocking' interrupted
Shutdown finished
```

[StackOverFlow](http://stackoverflow.com/questions/2541597/how-to-gracefully-handle-the-sigkill-signal-in-java/33778483#33778483)
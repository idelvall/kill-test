/*
 * Copyright 2015 Ignacio del Valle Alles idelvall@brutusin.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.kill;

import java.util.Set;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class KillTest {

    /**
     * For testing purposes (overloads CPU). Emulates a waiting operation with using blocking API.
     * @param milis 
     */
    private static void waitNonBlocking(long milis) {
        long start = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - start > milis) {
                break;
            }
        }
    }

    private static void nonBlockingProcess() throws InterruptedException {
        for (int i = 0; true; i++) {
            waitNonBlocking(7000);
            System.out.println("Checking interrupted");
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    private static void blockingProcess() throws InterruptedException {
        synchronized (KillTest.class) {
            KillTest.class.wait();
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Interrupting threads");
                Set<Thread> runningThreads = Thread.getAllStackTraces().keySet();
                for (Thread th : runningThreads) {
                    if (th != Thread.currentThread() && !th.isDaemon() && th.getClass().getName().startsWith("org.brutusin")) {
                        System.out.println("Interrupting '" + th.getClass() + "' termination");
                        th.interrupt();
                    }
                }
                for (Thread th : runningThreads) {
                    try {
                        if (th != Thread.currentThread() && !th.isDaemon() && th.isInterrupted()) {
                            System.out.println("Waiting '" + th.getName() + "' termination");
                            th.join();
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("Shutdown interrupted");
                    }
                }
                System.out.println("Shutdown finished");
            }
        });
        Thread t1 = new Thread("non-blocking") {
            @Override
            public void run() {
                try {
                    System.out.println("Running '" + Thread.currentThread().getName()+"'");
                    nonBlockingProcess();
                } catch (InterruptedException ex) {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        Thread t2 = new Thread("blocking") {
            @Override
            public void run() {
                try {
                    System.out.println("Running '" + Thread.currentThread().getName()+"'");
                    blockingProcess();
                } catch (InterruptedException ex) {
                    System.out.println("Thread '" + Thread.currentThread().getName() + "' interrupted");
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };

        t1.start();
        t2.start();
    }
}

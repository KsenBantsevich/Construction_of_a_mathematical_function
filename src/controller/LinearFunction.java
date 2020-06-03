package controller;

import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.currentThread;

public class LinearFunction implements Runnable {

    private Integer x;
    private final Integer rightLimit;
    private final ConcurrentLinkedQueue<Integer> queue;

    public LinearFunction(Integer leftThreshold, Integer rightThreshold,
                          ConcurrentLinkedQueue<Integer> queue) {

        this.x = leftThreshold;
        this.rightLimit = rightThreshold;
        this.queue = queue;

    }

    @Override
    public void run() {
        while (x <= rightLimit && !Thread.interrupted()) {
            int y = 2*x;

            queue.add(y);

            try {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {
                break;
            }

            x += 1;
        }
        currentThread().interrupt();
    }

}

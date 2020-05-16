package controller;


import model.Point;
import view.Table;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Thread.currentThread;

public class SortFunction implements Runnable {

    private static final int STRAP_DIGIT = 10;
    private final ConcurrentLinkedQueue<Integer> queue;
    private final Table table;

    private int arrayLength;
    private int amountOfArrays;

    private List<Point> data;

    private int sleepTime;
    private int peakLimit;

    public SortFunction(Integer arrayLength, Integer amountOfArrays, ConcurrentLinkedQueue<Integer> queue, Table table) {

        data = new ArrayList<>();
        this.arrayLength = arrayLength;
        this.amountOfArrays=amountOfArrays;
        this.queue = queue;
        this.table = table;
        sleepTime = 500;
        peakLimit = 2000;

    }

    @Override
    public void run() {
        for (int currentSize = 2; currentSize < arrayLength; currentSize++) {
            int commonTime = 0;
            for (int currentArrayCount = 1; currentArrayCount < amountOfArrays; currentArrayCount++) {
                commonTime += sortTime(generateRandomArray(currentSize));
            }
            int averageTime = commonTime / amountOfArrays;

            table.updateTable(new Point(currentSize, averageTime));
            queue.add(averageTime);

            try {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {
                break;
            }

        }
        currentThread().interrupt();


    }

    private int[] selectionSort(int[] arr) {
        int min, temp;
        for (int i = 0; i < arr.length - 1; i++)
        {
            min = i;
        for (int j = i + 1; j < arr.length; j++)
        {
            if (arr[j] < arr[min])
                min = j;
        }
            temp = arr[i];
            arr[i] =arr[min];
            arr[min] = temp;
        }
        return  arr;
    }

    private long sortTime(int[] arrayToSort) {
        long startTime = System.nanoTime() / STRAP_DIGIT;
        selectionSort(arrayToSort);
        long endTime = System.nanoTime() / STRAP_DIGIT;
        long result = endTime - startTime;
        if (result > peakLimit) {
            result = sortTime(arrayToSort);
        }
        return result;
    }

    private int[] generateRandomArray(int currentArraySize) {
        int[] result = new int[currentArraySize];
        Random random = new Random();
        for (int i = 0; i < result.length; i++) {
            result[i] = random.nextInt(currentArraySize);
        }
        return result;
    }

}
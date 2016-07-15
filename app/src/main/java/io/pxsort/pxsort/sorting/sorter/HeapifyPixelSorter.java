package io.pxsort.pxsort.sorting.sorter;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.Arrays;

import io.pxsort.pxsort.sorting.filter.Filter;

/**
 * A PixelSorter that organizes pixels in a binary heap.
 * <p/>
 * Created by George on 2016-05-10.
 */
class HeapifyPixelSorter extends PixelSorter {

    HeapifyPixelSorter(Filter filter, Context context) {
        super(filter, context);
    }

    @Override
    protected void pixelSort(Bitmap partition) {

        int[] oldPixels = new int[partition.getWidth() * partition.getHeight()];
        partition.getPixels(oldPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());

        int[] newPixels = Arrays.copyOf(oldPixels, oldPixels.length);
        newPixels = buildHeap(newPixels, filter.component, filter.order);

        for (int i = 0; i < newPixels.length; i++) {
            newPixels[i] = combinePixels(oldPixels[i], newPixels[i]);
        }

        partition.setPixels(newPixels, 0, partition.getWidth(),
                0, 0, partition.getWidth(), partition.getHeight());
    }

    private int[] buildHeap(int[] heap, int component, int order) {

        for (int i = heap.length / 2; i >= 0; i--) {
            heapify(heap, component, order, i);
        }

        return heap;
    }

    private void heapify(int[] heap, int component, int order, int index) {
        int leftIndex = leftChildIndex(index);
        int rightIndex = rightChildIndex(index);

        int largest = index;

        if (rightIndex < heap.length) {
            switch (order) {
                case Filter.DESCENDING:
                    if (getComponent(heap[leftIndex], component)
                            > getComponent(heap[index], component)) {
                        largest = leftIndex;
                    }
                    if (getComponent(heap[rightIndex], component)
                            > getComponent(heap[largest], component)) {
                        largest = rightIndex;
                    }
                    break;

                case Filter.ASCENDING:
                    if (getComponent(heap[leftIndex], component)
                            < getComponent(heap[index], component)) {
                        largest = leftIndex;
                    }
                    if (getComponent(heap[rightIndex], component)
                            < getComponent(heap[largest], component)) {
                        largest = rightIndex;
                    }
                    break;
            }
        }

        if (largest != index) {
            swap(heap, largest, index);
            heapify(heap, component, order, largest);
        }
    }

    private void swap(int[] heap, int index1, int index2) {
        int temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    private int leftChildIndex(int parentIndex) {
        return 2 * parentIndex;
    }

    private int rightChildIndex(int parentIndex) {
        return 2 * parentIndex + 1;
    }
}

package com.ftpix.mmath.cron.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.function.Consumer;

public class BatchProcessor<T> {
    protected Log logger = LogFactory.getLog(this.getClass());
    private final int batchSize;
    private BatchSupplier<T> supplier;
    private Consumer<List<T>> consumer;

    public BatchProcessor(int batchSize) {

        this.batchSize = batchSize;
    }

    public static <U> BatchProcessor<U> forClass(Class<? extends U> clazz, int batchSize) {
        return new BatchProcessor<U>(batchSize);
    }

    public BatchProcessor<T> withSupplier(BatchSupplier supplier) {
        this.supplier = supplier;
        return this;
    }

    public BatchProcessor<T> withProcessor(Consumer<List<T>> consumer) {
        this.consumer = consumer;
        return this;
    }


    public void start() {
        int batch = 1;
        int offset = 0;
        int totalProcessed = 0;
        List<T> batchData;
        do {
//            logger.info("Getting batch {} with offset {} and batch size {}", batch, offset, batchSize);
            batchData = supplier.getBatch(batch, batchSize, offset);
            consumer.accept(batchData);


            totalProcessed += batchData.size();
//            logger.info("Processed batch {}, Data count:{}, batch size {}, total processed: {}", batch, batchData.size(), batchSize, totalProcessed);
            batch++;
            offset = (batch - 1) * batchSize;

        } while (batchData.size() == batchSize);

    }

    /**
     * Interface to get a batch with given data to choose from
     */
    public interface BatchSupplier<T> {

        /**
         * Method to get a new batch of data
         *
         * @param batch     the batch number (aka page)
         * @param batchSize size of a single batch
         * @param offset    where to start from (can be useful for mysql
         * @return
         */
        List<T> getBatch(int batch, int batchSize, int offset);
    }
}

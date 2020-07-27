package org.apache.camel.component.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.support.ScheduledBatchPollingConsumer;
import org.apache.camel.util.CastUtils;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @program: camel-oss
 * @description: this is the description of the OSSConsumer class
 * @author: ahuan
 * @version: 2020-07-23 10:11
 **/
public class OSSConsumer extends ScheduledBatchPollingConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(OSSConsumer.class);

    public OSSConsumer(OSSEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
    String marker;
    @Override
    public int processBatch(Queue<Object> exchanges) throws Exception {
        int total = exchanges.size();

        for (int index = 0; index < total && isBatchAllowed(); index++) {
            // only loop if we are started (allowed to run)
            final Exchange exchange = ObjectHelper.cast(Exchange.class, exchanges.poll());
            // add current index and total as properties
            exchange.setProperty(Exchange.BATCH_INDEX, index);
            exchange.setProperty(Exchange.BATCH_SIZE, total);
            exchange.setProperty(Exchange.BATCH_COMPLETE, index == total - 1);

            // update pending number of exchanges
            pendingExchanges = total - index - 1;

            // add on completion to handle after work when the exchange is done
            exchange.adapt(ExtendedExchange.class).addOnCompletion(new Synchronization() {
                @Override
                public void onComplete(Exchange exchange) {
                    processCommit(exchange);
                }

                @Override
                public void onFailure(Exchange exchange) {
                    processRollback(exchange);
                }

                @Override
                public String toString() {
                    return "S3ConsumerOnCompletion";
                }
            });

            LOG.trace("Processing exchange [{}]...", exchange);
            getAsyncProcessor().process(exchange, new AsyncCallback() {
                @Override
                public void done(boolean doneSync) {
                    LOG.trace("Processing exchange [{}] done.", exchange);
                }
            });
        }

        return total;
    }

    @Override
    protected int poll() throws Exception {
        shutdownRunningTask = null;
        pendingExchanges = 0;

        String fileName = getConfiguration().getFileName();
        String bucketName = getConfiguration().getBucketName();
        Queue<Exchange> exchanges;

        if (fileName != null) {
            LOG.trace("Getting object in bucket [{}] with file name [{}]...", bucketName, fileName);

            OSSObject ossObject = getOSSClient().getObject(bucketName,  fileName);
            exchanges = createExchanges(ossObject);
        } else {
            LOG.trace("Queueing objects in bucket [{}]...", bucketName);

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(bucketName);
            listObjectsRequest.withPrefix(getConfiguration().getPrefix());
            listObjectsRequest.setDelimiter(getConfiguration().getDelimiter());

            if (maxMessagesPerPoll > 0) {
                listObjectsRequest.setMaxKeys(maxMessagesPerPoll);
            }
            // if there was a marker from previous poll then use that to
            // continue from where we left last time
            if (marker != null) {
                LOG.trace("Resuming from marker: {}", marker);
                listObjectsRequest.setMarker(marker);
            }

            ObjectListing objectListing = getOSSClient().listObjects(listObjectsRequest);

            if (objectListing.isTruncated()) {
                marker = objectListing.getNextMarker();
                LOG.trace("Returned list is truncated, so setting next marker: {}", marker);
            } else {
                // no more data so clear marker
                marker = null;
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Found {} objects in bucket [{}]...", objectListing.getObjectSummaries().size(), bucketName);
            }

            exchanges = createExchanges(objectListing.getObjectSummaries());
        }
        return processBatch(CastUtils.cast(exchanges));
    }

    protected Queue<Exchange> createExchanges(List<OSSObjectSummary> ossObjectSummaries) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Received {} messages in this poll", ossObjectSummaries.size());
        }

        Collection<OSSObject > ossObjects = new ArrayList<>();
        Queue<Exchange> answer = new LinkedList<>();
        try {
            for (OSSObjectSummary ossObjectSummary : ossObjectSummaries) {
                OSSObject ossObject = getOSSClient()
                        .getObject(ossObjectSummary.getBucketName(),ossObjectSummary.getKey());

                //if (includeOSSObject(ossObject)) {
                    ossObjects.add(ossObject);
                    Exchange exchange = getEndpoint().createExchange(ossObject);
                    answer.add(exchange);
                //}
            }
        } catch (Throwable e) {
            LOG.warn("Error getting S3Object due: {}", e.getMessage(), e);
            // ensure all previous gathered s3 objects are closed
            // if there was an exception creating the exchanges in this batch
            ossObjects.forEach(IOHelper::close);
            throw e;
        }

        return answer;
    }
    protected boolean includeOSSObject(OSSObject ossObject) {
            //Config says to ignore folders/directories
            return !"application/x-directory".equalsIgnoreCase(ossObject.getObjectMetadata().getContentType());
    }
    protected Queue<Exchange> createExchanges(OSSObject ossObject) {
        Queue<Exchange> answer = new LinkedList<>();
        Exchange exchange = getEndpoint().createExchange(ossObject);
        answer.add(exchange);
        return answer;
    }
    protected OSSConfiguration getConfiguration() {
        return getEndpoint().getConfiguration();
    }
    protected void processCommit(Exchange exchange) {
        /*try {
            if (getConfiguration().isMoveAfterRead()) {
                String bucketName = exchange.getIn().getHeader(AWS2S3Constants.BUCKET_NAME, String.class);
                String key = exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class);

                LOG.trace("Moving object from bucket {} with key {} to bucket {}...", bucketName, key, getConfiguration().getDestinationBucket());

                getAmazonS3Client().copyObject(CopyObjectRequest.builder().destinationKey(key).destinationBucket(getConfiguration().getDestinationBucket()).copySource(bucketName + "/" + key).build());

                LOG.trace("Moved object from bucket {} with key {} to bucket {}...", bucketName, key, getConfiguration().getDestinationBucket());
            }
            if (getConfiguration().isDeleteAfterRead()) {
                String bucketName = exchange.getIn().getHeader(AWS2S3Constants.BUCKET_NAME, String.class);
                String key = exchange.getIn().getHeader(AWS2S3Constants.KEY, String.class);

                LOG.trace("Deleting object from bucket {} with key {}...", bucketName, key);

                getAmazonS3Client().deleteObject(DeleteObjectRequest.builder().bucket(getConfiguration().getBucketName()).key(key).build());

                LOG.trace("Deleted object from bucket {} with key {}...", bucketName, key);
            }
        } catch (AwsServiceException e) {
            getExceptionHandler().handleException("Error occurred during moving or deleting object. This exception is ignored.", exchange, e);
        }*/
    }

    /**
     * Strategy when processing the exchange failed.
     *
     * @param exchange the exchange
     */
    protected void processRollback(Exchange exchange) {
        Exception cause = exchange.getException();
        if (cause != null) {
            LOG.warn("Exchange failed, so rolling back message status: {}", exchange, cause);
        } else {
            LOG.warn("Exchange failed, so rolling back message status: {}", exchange);
        }
    }
    protected OSS getOSSClient() {
        return getEndpoint().getOssClient();
    }

    @Override
    public OSSEndpoint getEndpoint() {
        return (OSSEndpoint)super.getEndpoint();
    }

}
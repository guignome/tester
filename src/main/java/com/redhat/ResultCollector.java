package com.redhat;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

@ApplicationScoped
public class ResultCollector {
    private AtomicInteger requestCounter = new AtomicInteger(0);
    private ArrayList<Result> results = new ArrayList<>();
    private static String SEPARATOR = "----------";

    static final String pattern = "yyyy-MM-dd hh:mm:ss.SSS";
    static final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    private static class Result<T> {
        int requestId;
        Date sentTime;
        Date receivedTime;
        HttpRequest<T> request;
        HttpResponse<T> response;

        Result(int requestId, Date sentTime, HttpRequest<T> request) {
            this.requestId = requestId;
            this.sentTime = sentTime;
            this.request = request;

        }

        public long duration() {
            return ChronoUnit.MILLIS.between(sentTime.toInstant(),receivedTime.toInstant());
        }
    }

    public void init() {
        Log.debug("Result Init");
        requestCounter = new AtomicInteger(0);
        results = new ArrayList<>();
    }

    public int size() {
        return results.size();
    }

    public long minDuration() {
        return results.stream().mapToLong(r->r.duration()).summaryStatistics().getMin();
    }

    public long maxDuration() {
        return results.stream().mapToLong(r->r.duration()).summaryStatistics().getMax();
    }

    public double averageDuration() {
        return results.stream().mapToLong(r->r.duration()).summaryStatistics().getAverage();
    }

    public int onRequestSent(HttpRequest request) {
        int requestId = requestCounter.getAndIncrement();
        Log.debug("Request " + requestId);
        results.add(requestId, new Result(requestId, new Date(), request));
        return requestId;
    }

    public void onResponseReceived(int requestId, HttpResponse response) {
        Log.debug("Response " + requestId);
        System.out.println(renderResponse(response));
        results.get(requestId).receivedTime = new Date();
        results.get(requestId).response = response;
    }

    public void onFailureReceived(int requestId,Throwable t) {
        Log.debug("Response " + requestId);
        results.get(requestId).receivedTime = new Date();
        results.get(requestId).response = null;
        System.out.println("Received error: " + t.getMessage());
        Log.error("Received error: ", t);
        return;
    }

    public String renderCSV() {
        Log.debug("Render CSV.");
        StringBuffer sb = new StringBuffer();
        sb.append("ID,")
          .append("Sent Time,")
          .append("Received Time,")
          .append("Duration (ms),")
          .append("Response code,")
          .append("Received Body\n");
        for (Result r : results) {
            sb.append(r.requestId).append(',')
                    .append(dateFormat.format(r.sentTime)).append(',')
                    .append(dateFormat.format(r.receivedTime)).append(',')
                    .append(ChronoUnit.MILLIS.between(r.sentTime.toInstant(),r.receivedTime.toInstant())).append(',');
            if(r.response == null) {
                sb.append("null,");
                sb.append("null\n");
            } else {
                sb.append(r.response.statusCode()).append(',');
                sb.append(r.response.bodyAsString()).append('\n');
            }
        }
        return sb.toString();
    }

     public static String renderResponse(HttpResponse<Buffer> response) {
        StringBuilder sb = new StringBuilder()
                .append(SEPARATOR).append(" HTTP ").append(response.statusCode()).append(' ').append(SEPARATOR).append('\n')
                .append(SEPARATOR).append(" Headers  ").append(SEPARATOR).append('\n');
        response.headers().forEach(
                (k, v) -> {
                    sb.append(k).append(" : ").append(v).append("\n");
                });
        sb.append(SEPARATOR).append("   Body   ").append(SEPARATOR).append('\n')
                .append(response.bodyAsString())
                .append('\n').append(SEPARATOR).append("    END   ").append(SEPARATOR).append('\n');
        return sb.toString();
    }


}
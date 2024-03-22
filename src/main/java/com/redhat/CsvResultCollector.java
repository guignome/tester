package com.redhat;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.quarkus.logging.Log;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

public class CsvResultCollector implements ResultCollector {
    private AtomicInteger requestCounter = new AtomicInteger(0);
    private ArrayList<Result<?>> results = new ArrayList<>();
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

        public int statusCode() {
            return response == null ? 0 : response.statusCode();
        }

        public long duration() {
            return ChronoUnit.MILLIS.between(sentTime.toInstant(), receivedTime.toInstant());
        }
    }

    public void init() {
        Log.debug("Initializing CsvResultCollector.");
        requestCounter = new AtomicInteger(0);
        results = new ArrayList<>();
    }

    public int size() {
        return results.size();
    }

    public long minDuration() {
        return results.stream().mapToLong(r -> r.duration()).summaryStatistics().getMin();
    }

    public long maxDuration() {
        return results.stream().mapToLong(r -> r.duration()).summaryStatistics().getMax();
    }

    public double averageDuration() {
        return results.stream().mapToLong(r -> r.duration()).summaryStatistics().getAverage();
    }

    public int onRequestSent(HttpRequest<?> request) {
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

    public void onFailureReceived(int requestId, Throwable t) {
        Log.debug("Response " + requestId);
        results.get(requestId).receivedTime = new Date();
        results.get(requestId).response = null;
        System.out.println("Received error: " + t.getMessage());
        Log.error("Received error: ", t);
        return;
    }

    public void render(Writer w) throws IOException {
        Log.debug("Render CSV.");
        w.append("ID,")
                .append("Sent Time,")
                .append("Received Time,")
                .append("Duration (ms),")
                .append("Response code,")
                .append("Received Body\n");
        for (Result r : results) {
            w.append(String.valueOf(r.requestId)).append(',')
                    .append(dateFormat.format(r.sentTime)).append(',')
                    .append(dateFormat.format(r.receivedTime)).append(',')
                    .append(String
                            .valueOf(ChronoUnit.MILLIS.between(r.sentTime.toInstant(), r.receivedTime.toInstant())))
                    .append(',');
            if (r.response == null) {
                w.append("null,");
                w.append("null\n");
            } else {
                w.append(String.valueOf(r.response.statusCode())).append(',');
                w.append(r.response.bodyAsString()).append('\n');
            }
        }
    }

    public String renderSummary() {
        //key is http code, value is the count
        Map<Integer,Integer> statusCodesCount = new HashMap<>();
        
        results.forEach(r->{
            //Increment the count in the map ( 400: i++, )
            statusCodesCount.put(r.statusCode(),statusCodesCount.getOrDefault(r.statusCode(), 0)+1);
        });
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Requests sent. Duration (ms): min=%d, max=%d, avg=%.3f",
            size(), minDuration(), maxDuration(),
            averageDuration()))
            .append("\n")
            .append("HTTP Return Codes: { ");
        for(int code:statusCodesCount.keySet()){
            sb.append(code).append(":").append(statusCodesCount.get(code))
             .append(' ');
        }
        sb.append('}');
            
        return sb.toString();
    }

    public static String renderResponse(HttpResponse<Buffer> response) {
        StringBuilder sb = new StringBuilder()
                .append(SEPARATOR).append(" HTTP ").append(response.statusCode()).append(' ').append(SEPARATOR)
                .append('\n')
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

    @Override
    public String getFormat() {
        return FORMAT_CSV;
    }

}
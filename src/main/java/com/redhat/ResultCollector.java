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
    }

    public void init() {
        Log.debug("Result Init");
        requestCounter = new AtomicInteger(0);
        results = new ArrayList<>();
    }

    public int size() {
        return results.size();
    }

    public int onRequestSent(HttpRequest request) {
        int requestId = requestCounter.getAndIncrement();
        Log.debug("Request " + requestId);
        results.add(requestId, new Result(requestId, new Date(), request));
        return requestId;
    }

    public void onResponseReceived(int requestId, HttpResponse response) {
        Log.debug("Response " + requestId);
        Log.info(renderResponse(response));
        results.get(requestId).receivedTime = new Date();
        results.get(requestId).response = response;
    }

    public String renderCSV() {
        Log.debug("Render CSV.");
        StringBuffer sb = new StringBuffer();
        sb.append("ID,")
          .append("Sent Time,")
          .append("Received Time,")
          .append("Duration (ms),")
          .append("Received Body\n");
        for (Result r : results) {
            sb.append(r.requestId).append(',')
                    .append(dateFormat.format(r.sentTime)).append(',')
                    .append(dateFormat.format(r.receivedTime)).append(',')
                    .append(ChronoUnit.MILLIS.between(r.sentTime.toInstant(),r.receivedTime.toInstant())).append(',');
            if(r.response == null) {
                sb.append("null\n");
            } else {
                sb.append(r.response.bodyAsString()).append('\n');
            }
        }
        return sb.toString();
    }

     public static String renderResponse(HttpResponse<Buffer> response) {
        StringBuilder sb = new StringBuilder()
                .append("-----\n")
                .append("HTTP Status code: ").append(response.statusCode())
                .append("\n----- Headers -----\n");
        response.headers().forEach(
                (k, v) -> {
                    sb.append(k).append(" : ").append(v).append("\n");
                });
        sb.append("----- Body -----  ").append("\n")
                .append(response.bodyAsString()).append("\n")
                .append("-----\n");
        return sb.toString();
    }
}
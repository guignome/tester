package com.redhat.tester.results;

import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;
import io.vertx.ext.web.client.HttpResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CsvResultCollector implements ResultCollector {
    private ArrayList<Result> results = new ArrayList<>();
    private Writer writer;
    private String fileName;

    static final String pattern = "yyyy-MM-dd hh:mm:ss.SSS";
    static final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    private static class Result {
        int requestId;
        Date sentTime;
        Date receivedTime;
        HttpResponse<?> response;

        Result(int requestId, Date sentTime) {
            this.requestId = requestId;
            this.sentTime = sentTime;
        }

        public int statusCode() {
            return response == null ? 0 : response.statusCode();
        }

        public long duration() {
            return ChronoUnit.MILLIS.between(sentTime.toInstant(), receivedTime.toInstant());
        }
    }
    @Override
    public String getResultFileName() {
        return fileName;
    }

    @Override
    public void init(ConfigurationModel model) {
        Log.debug("Initializing CsvResultCollector.");
        this.fileName = model.results.filename;
        results = new ArrayList<>();
        File result = createResultFile(fileName);

        // Prepare the result output
        try {
            this.writer = new FileWriter(result);
        } catch (IOException e) {
            Log.error("Not able to create Output result file.", e);
        }
    }

    @Override
    public int size() {
        return results.size();
    }

    public long minDuration() {
        return results.stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getMin();
    }

    public long maxDuration() {
        return results.stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getMax();
    }

    public double averageDuration() {
        return results.stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getAverage();
    }

    public static final String REQUEST_ID = "request_id";

    @Override
    public void beforeStep(Step step, Map<String, Object> ctx) {
        int requestId = (int) ctx.get(REQUEST_ID);
        Log.debug("Request " + requestId);
        results.add(requestId, new Result(requestId, new Date()));
    }

    @Override
    public void afterStep(Step step, Map<String, Object> ctx) {
        int requestId = (int) ctx.get(REQUEST_ID);
        Log.debug("Response " + requestId);
        results.get(requestId).receivedTime = new Date();
        Object response = ctx.get(ClientRunner.RESULT_VAR);
        if(response instanceof HttpResponse) {
            results.get(requestId).response = (HttpResponse<?>) response;
        } else {
            results.get(requestId).response = null;
        }
        results.get(requestId).response = (HttpResponse<?>) ctx.get(ClientRunner.RESULT_VAR);
    }

    @Override
    public void afterSuite(Suite suite, Map<String, Object> ctx) {
    }

    private void render(Writer w) throws IOException {
        Log.debug("Render CSV.");
        w.append("ID,")
                .append("Sent Time,")
                .append("Received Time,")
                .append("Duration (ms),")
                .append("Response code,")
                .append("Received Body\n");
        for (Result r : results) {
            w.append(String.valueOf(r.requestId)).append(',')
                    .append(dateFormat.format(r.sentTime)).append(',');
            if (r.receivedTime != null) {
                w.append(dateFormat.format(r.receivedTime))
                        .append(',')
                        .append(String
                                .valueOf(
                                        ChronoUnit.MILLIS.between(r.sentTime.toInstant(), r.receivedTime.toInstant())));
            } else {
                w.append(',');
            }

            w.append(',');
            if (r.response == null) {
                w.append("null,");
                w.append("null\n");
            } else {
                w.append(String.valueOf(r.response.statusCode())).append(',');
                w.append(r.response.bodyAsString()).append('\n');
            }
        }
    }

    @Override
    public String renderSummary() {
        // key is http code, value is the count
        Map<Integer, Integer> statusCodesCount = new HashMap<>();

        results.forEach(r -> {
            // Increment the count in the map ( 400: i++, )
            statusCodesCount.put(r.statusCode(), statusCodesCount.getOrDefault(r.statusCode(), 0) + 1);
        });
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s Requests sent. Duration (ms): min=%d, max=%d, avg=%.3f",
                size(), minDuration(), maxDuration(),
                averageDuration()))
                .append("\n")
                .append("HTTP Return Codes: { ");
        for (int code : statusCodesCount.keySet()) {
            sb.append(code).append(":").append(statusCodesCount.get(code))
                    .append(' ');
        }
        sb.append('}');

        return sb.toString();
    }

    @Override
    public String getFormat() {
        return FORMAT_CSV;
    }

    @Override
    public void close() {
        try {
            render(writer);
            writer.close();
        } catch (IOException e) {
            Log.error(e);
        }
    }

    @Override
    public ResultSummary getCurrentResultSummary() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentResultSummary'");
    }

}
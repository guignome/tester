package com.redhat.tester.results;

import com.redhat.tester.ClientRunner;
import com.redhat.tester.ConfigurationModel;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite;
import com.redhat.tester.ConfigurationModel.ClientConfiguration.Suite.Step;
import io.quarkus.logging.Log;
import io.vertx.ext.web.client.HttpResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.redhat.tester.ClientRunner.REQUEST_ID_VAR;
import static com.redhat.tester.ClientRunner.CLIENT_ID_VAR;


public class CsvResultCollector implements ResultCollector {
    private Map<String,Result> results = new HashMap<>();
    private Writer writer;
    private String fileName;

    static final String pattern = "yyyy-MM-dd hh:mm:ss.SSS";
    static final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    private static class Result {
        String requestId;
        Date sentTime;
        Date receivedTime;
        HttpResponse<?> response;

        Result(String requestId, Date sentTime) {
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
        results = new HashMap<>();
        Path result = createResultFile(fileName);

        // Prepare the result output
        try {
            this.writer = Files.newBufferedWriter(result);
        } catch (IOException e) {
            Log.error("Not able to create Output result file.", e);
        }
    }

    @Override
    public int size() {
        return results.size();
    }

    public long minDuration() {
        return results.values().stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getMin();
    }

    public long maxDuration() {
        return results.values().stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getMax();
    }

    public double averageDuration() {
        return results.values().stream()
        .filter(r -> r.receivedTime != null && r.sentTime != null)
        .mapToLong(r -> r.duration()).summaryStatistics().getAverage();
    }

    private static String requestKey(String clientId, String requestId) {
        return clientId + '_' + requestId;
    }

    @Override
    public void beforeStep(Step step, Map<String, Object> ctx) {
        String resultKey = requestKey((String) ctx.get(CLIENT_ID_VAR),(String) ctx.get(REQUEST_ID_VAR));
        Log.debug("Request " + resultKey);
        results.put(resultKey, new Result(resultKey, new Date()));
    }

    @Override
    public void afterStep(Step step, Map<String, Object> ctx) {
        String resultKey = requestKey((String) ctx.get(CLIENT_ID_VAR),(String) ctx.get(REQUEST_ID_VAR));
        Log.debug("Response " + resultKey);
        results.get(resultKey).receivedTime = new Date();
        Object response = ctx.get(ClientRunner.RESULT_VAR);
        if(response instanceof HttpResponse) {
            results.get(resultKey).response = (HttpResponse<?>) response;
        } else {
            results.get(resultKey).response = null;
        }
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
        for (Result r : results.values()) {
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

        results.values().forEach(r -> {
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
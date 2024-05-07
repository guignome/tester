package com.redhat;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.redhat.ResultCollector.AfterStepPayload;
import com.redhat.ResultCollector.AfterSuitePayload;
import com.redhat.ResultCollector.BeforeStepPayload;
import com.redhat.ResultCollector.InitPayload;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;

@ApplicationScoped
public class Factory {
    @Inject
    Vertx vertx;

    @Inject
    TemplateRenderer renderer;

    @Inject
    Endpoints endpoints;

    private boolean codecsRegistered = false;
    private List<MessageConsumer> consumers = new ArrayList<>();

    String format = ResultCollector.FORMAT_CSV;

    // public void setFormat(String format) {
    // this.format = format;
    // }

    private ResultCollector resultCollector;

    public ClientRunner createClientRunner() {
        Log.debug("Creating ClientRunner.");
        ClientRunner client = new ClientRunner(vertx, endpoints);
        // client.setResultCollector(resultCollector);
        client.setRenderer(renderer);
        return client;
    }

    public ServerRunner createServerRunner() {
        Log.debug("Creating ServerRunner.");
        ServerRunner server = new ServerRunner(vertx);
        server.setRenderer(renderer);
        return server;
    }

    public ResultCollector getResultCollector() {
        // if (resultCollector == null || !resultCollector.getFormat().equals(format)) {
        // if (format == null || ResultCollector.FORMAT_CSV.equals(format)) {
        // resultCollector = new CsvResultCollector();
        // } else if (ResultCollector.FORMAT_JSON.equals(format)) {
        // resultCollector = new JsonResultCollector(renderer);
        // } else {
        // TpsResultCollector tps = new TpsResultCollector();
        // tps.setVertx(vertx);
        // resultCollector = tps;
        // }
        // }

        return resultCollector;
    }

    public void registerResultCollector(String format, String resultFile, ConfigurationModel model) {
        this.format = format;
        if (resultCollector == null || !resultCollector.getFormat().equals(format)) {
            if (format == null || ResultCollector.FORMAT_CSV.equals(format)) {
                resultCollector = new CsvResultCollector();
            } else if (ResultCollector.FORMAT_JSON.equals(format)) {
                resultCollector = new JsonResultCollector(renderer);
            } else {
                resultCollector = new TpsResultCollector(vertx);
            }
        }
        // register handlers on the event bus
        consumers.forEach((c) -> {
            c.unregister();
        });
        consumers.clear();

        consumers.add(vertx.eventBus().localConsumer(ResultCollector.BEFORE_STEP_ADDRESS)
                .handler((m) -> {
                    BeforeStepPayload payload = (BeforeStepPayload) m.body();
                    resultCollector.beforeStep(payload.step(), payload.ctx());
                }));
        consumers.add(vertx.eventBus().localConsumer(ResultCollector.AFTER_STEP_ADDRESS)
                .handler((m) -> {
                    AfterStepPayload payload = (AfterStepPayload) m.body();
                    resultCollector.afterStep(payload.step(), payload.ctx());
                }));
        consumers.add(vertx.eventBus().localConsumer(ResultCollector.AFTER_SUITE_ADDRESS)
                .handler((m) -> {
                    AfterSuitePayload payload = (AfterSuitePayload) m.body();
                    resultCollector.afterSuite(payload.suite(), payload.ctx());
                }));
        consumers.add(vertx.eventBus().localConsumer(ResultCollector.INIT_ADDRESS)
                .handler((m) -> {
                    InitPayload payload = (InitPayload) m.body();
                    resultCollector.init(payload.file(), payload.model());
                }));
        consumers.add(vertx.eventBus().localConsumer(ResultCollector.CLOSE_ADDRESS)
                .handler((m) -> {
                    resultCollector.close();
                }));
        consumers.add(vertx.eventBus().localConsumer(ResultCollector.SUMMARY_ADDRESS)
                .handler((m) -> {
                    m.reply(resultCollector.renderSummary());
                }));
        // register codecs
        if (!codecsRegistered) {
            vertx.eventBus().registerDefaultCodec(ResultCollector.BeforeStepPayload.class,
                    new MessageCodecs.BeforeStepPayloadCodec());
            vertx.eventBus().registerDefaultCodec(ResultCollector.AfterStepPayload.class,
                    new MessageCodecs.AfterStepPayloadCodec());

            vertx.eventBus().registerDefaultCodec(ResultCollector.AfterSuitePayload.class,
                    new MessageCodecs.AfterSuitePayloadCodec());
            vertx.eventBus().registerDefaultCodec(ResultCollector.InitPayload.class,
                    new MessageCodecs.InitPayloadCodec());
            codecsRegistered = true;
        }

        // send the init signal
        vertx.eventBus().send(ResultCollector.INIT_ADDRESS, new InitPayload(resultFile, model));
    }
}

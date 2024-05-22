package com.redhat.tester.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.redhat.tester.ConfigurationModel;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;;

public class UIServer {
    Vertx vertx;
    TesterApi api;

    public UIServer(Vertx vertx, TesterApi api) {
        this.vertx = vertx;
        this.api  = api;
    }

    public void init() {
        // Start the static content router:
        Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());
       
        vertx.createHttpServer().webSocketHandler(ws -> {
            System.out.println("Web Socket Connected.");
            
            ws.textMessageHandler(text->{
                System.out.println("Received: " + text);
                //Create JSON object
                ObjectMapper mapper = new ObjectMapper();
                Command command = null;
                try {
                    command = mapper.readValue(text, Command.class);
                } catch (JsonProcessingException e) {
                    Log.error(e);
                }
                switch(command.command) {
                    case "init":
                        System.out.println("Received init message.");
                        break;
                    case "execute":
                        // api.execute(command.data.step,
                        //     command.data.variables,
                        //     command.data.repeat,
                        //     command.data.parallel,ws);
                        break;

                }
            });
        }).requestHandler(router).listen(8081);
        System.out.println("UIServer started.");
    }

}

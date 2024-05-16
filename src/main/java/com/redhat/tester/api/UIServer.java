package com.redhat.tester.api;

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

        // Start the socksjs router

        // SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        // sockJSHandler.bridge(new SockJSBridgeOptions());

        // router.route("/eventbus/*")
        //         .subRouter(sockJSHandler.socketHandler(sockJSSocket -> {

        //             // Just echo the data back
        //             sockJSSocket.handler(sockJSSocket::write);

        //         }));
        
        vertx.createHttpServer().webSocketHandler(ws -> {
            System.out.println("Web Socket Connected.");
            
            ws.textMessageHandler(text->{
                System.out.println("Received: " + text);
            });
        }).requestHandler(router).listen(8081);
        System.out.println("UIServer started.");
    }
}

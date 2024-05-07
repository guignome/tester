package com.redhat;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class UIServer {
    Vertx vertx;

    public UIServer(Vertx vertx) {
        this.vertx = vertx;
    }

    public void init() {
        //Start the static content router:
        Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());
            

        //Start the socksjs router

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        sockJSHandler.bridge(new SockJSBridgeOptions());

        router.route("/eventbus/*")
                .subRouter(sockJSHandler.socketHandler(sockJSSocket -> {

                    // Just echo the data back
                    sockJSSocket.handler(sockJSSocket::write);

                }));

        vertx.createHttpServer()
            .requestHandler(router).listen(8081);
        System.out.println("UIServer started.");
    }
}

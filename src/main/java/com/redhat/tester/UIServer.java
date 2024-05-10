package com.redhat.tester;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import jakarta.websocket.server.*;;

public class UIServer {
    Vertx vertx;

    public UIServer(Vertx vertx) {
        this.vertx = vertx;
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
        } )
                .requestHandler(router).listen(8081);
        System.out.println("UIServer started.");

        try {
            Session session = ContainerProvider.getWebSocketContainer().connectToServer(UIServer.class, new URI("ws://localhost:8081"));
            
        } catch (DeploymentException | IOException | URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

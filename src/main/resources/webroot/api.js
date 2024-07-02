/**
 * A JSON message Server->Client
 * @typedef ClientMessage
 * @property {string} kind The kind of data. Will be used to dispatch the message to the right handler.
 * @property {String} resourceType The resource type this message is targettted for.
 * @property {String} resourceInstance The resource instance this message is targettted for.
 * @property {any} data The data of the message, will be received by the handler.
 */

/**
 * A JSON message Client->Server
 * @typedef ServerMessage
 * @property {string} kind The kind of data. Will be used to dispatch the message to the right handler.
 * @property {any} data The data of the message, will be received by the handler.
 */

/**
 * A Single result set
 * @typedef ResultSet
 * @property {string} name The name of the resultset, is also typically the file name.
 * @property {any} data The data of the message, will be received by the handler.
 */


//Start the websocket
const wsprotocol = location.protocol === "http:" ? "ws:": "wss:";
const ws = new WebSocket(wsprotocol + "//" + location.host );
const handlers = new Map();
const views = new Map();

ws.onopen = function () {
    console.log("Websocket Opened.");
    /**
     * @type {ServerMessage}
     */
    let init = { kind: "init" };
    ws.send(JSON.stringify(init));
    ws.onmessage = onmessage;
}

/**
 * Generic handler for incoming websocket messages.
 * @param {MessageEvent<any>} event 
 */
function onmessage(event) {
    console.log("Received message: " + event.data);
    /**
     * @type {ClientMessage}
     */
    const msg = JSON.parse(event.data);
    const kind = msg.kind;
    if (handlers.has(kind)) {
        //execute the handler if there is one.

        handlers.get(kind)(msg);
    }
}

const api = {
    /**
     * Start the client and server defined in a model.
     * @param {*} model The model to start
     */
    startModel(model) {
        console.log("Starting Model");
        /** @type {ServerMessage} */
        let msg = {
            kind: "startModel",
            data: { model }
        };
        sendWhenReady(JSON.stringify(msg));
    },

    stopModel() {
        console.log("Stopping");
        /** @type {ServerMessage} */
        let msg = {
            kind: "stopModel",
            data: {}
        };
        sendWhenReady(JSON.stringify(msg));
    },

    /**
     * Registers a handler for a message kind.
     * @param {Function} handler 
     * @param {string} kind 
     */
    registerHandler(kind, handler) {
        handlers.set(kind, handler);
    },
    /** Watch the given resource
     * @param {string} resource The resource to watch
     * @param {Function} handler Then handler to execute.
     * @param {any} params Extra parameters
     */
    watch(resourceType,resourceInstance, handler) {
        console.log(`Watching ${resourceType}/${resourceInstance}`);
        let msg = {
            kind: "watch",
            resourceType,
            resourceInstance,
            data: { }
        };
        this.registerView(resourceType, resourceInstance, handler);
        sendWhenReady(JSON.stringify(msg));
    },
    stopWatch(resourceType,resourceInstance){
        console.log(`Stop Watching ${resourceType}/${resourceInstance}`);
        let msg = {
            kind: "stopWatch",
            resourceType,
            resourceInstance,
            data: { }
        };
        this.unregisterView(resourceType, resourceInstance);
        sendWhenReady(JSON.stringify(msg));
    },
    registerView(resourceType, resourceInstance, handler) {
        views.set(resourceType + "/" + resourceInstance, handler);
    },
    unregisterView(resourceType, resourceInstance) {
        views.delete(resourceType + "/" + resourceInstance);
    }
}

api.registerHandler("init", (msg) => {
    console.log("Received init message: " + msg);
})

api.registerHandler("clientStatus", (msg) => {
    console.log("Received clientStatus message: " + msg);
})

api.registerHandler("viewUpdate",
    /**
     * Calls the handler for the given resource.
     * @param {ClientMessage} msg 
     */
    (msg) => {
        const key = msg.resourceType + "/" + msg.resourceInstance;
        if (views.has(key)) {
            views.get(key)(msg);
        } else {
            console.warn("Received view update for resource but no handler registered: " + key);
        }
    })

function sendWhenReady(msg) {
    if (ws.readyState == ws.OPEN) {
        ws.send(msg);
    } else {
        ws.addEventListener("open", (event) => { ws.send(msg) });

    }
}

export default api;
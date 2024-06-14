/**
 * A JSON message Server->Client
 * @typedef ClientMessage
 * @property {string} kind The kind of data. Will be used to dispatch the message to the right handler.
 * @property {any} data The data of the message, will be received by the handler.
 */

/**
 * A JSON message Client->Server
 * @typedef ServerMessage
 * @property {string} kind The kind of data. Will be used to dispatch the message to the right handler.
 * @property {any} data The data of the message, will be received by the handler.
 */


//Start the websocket
const ws = new WebSocket("ws://localhost:8081");
const handlers = new Map();

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
        handlers.get(kind)(msg.data);
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
        let msg = {kind: "startModel",
            data: {model}
        };
        ws.send(JSON.stringify(msg));
    },

    /**
 * Starts the client with the given parameters;
 * @param {*} step The step to execute.
 * @param {*} variables The variables to replace.
 * @param {number} repeat How many times to repeat.
 * @param {number} parallel Number of parallel clients.
 * @returns {string} reportName The name of the report of the test run.
 */
    startClient(step, variables, repeat, parallel) {
        console.log("Starting client.");
        /**@type {ServerMessage} */
        let msg = {
            kind: "startClient",
            data: {
                client: {
                    topology: {
                        local: {
                            parallel, repeat
                        }
                    },
                    suites: [
                        {
                            name: "singleSuite",
                            steps: [
                                step
                            ]
                        }
                    ]
                }, variables
            }
        };
        ws.send(JSON.stringify(msg));
    },
    /**
     * Registers a handler for a message kind.
     * @param {Function} handler 
     * @param {string} kind 
     */
    registerHandler(kind, handler) {
        handlers.set(kind, handler);
    },
    /**
     * Stops a running client.
     */
    stopClient() {
        console.log("Stopping client.");
        /**@type {ServerMessage} */
        let msg = {
            kind: "stopClient",
            data: {}
        };
        sendWhenReady(JSON.stringify(msg));
    },
    /** Watch the given resource
     * @param {string} resource The resource to watch
     * @param {Function} handler Then handler to execute.
     */
    watch(resource, handler) {
        console.log("Watching " + resource);
        let msg = {
            kind: "watch",
            data: { resource }
        };
        this.registerHandler("resourceState", handler);
        sendWhenReady(JSON.stringify(msg));
    }
}

api.registerHandler("init", (msg) => {
    console.log("Received init message: " + msg);
})

api.registerHandler("clientStatus", (msg) => {
    console.log("Received clientStatus message: " + msg);
})

function sendWhenReady(msg) {
    if (ws.readyState == ws.OPEN) {
        ws.send(msg);
    } else {
        ws.addEventListener("open", (event) => { ws.send(msg) });

    }
}

export default api;
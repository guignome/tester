export type MessageHandler=  (msg:ClientMessage) => void

/**
 * A JSON message Server->Client
 */
export interface ClientMessage {
    kind: ClientMessageKind;
    resourceType?: ResourceType;
    resourceInstance?: String;
    data?: any;
}

/**
 * A JSON message Client->Server
 */
export interface ServerMessage {
    kind: ServerMessageKind;
    resourceType?: ResourceType;
    resourceInstance?: String;
    data?: any;
}

export const enum ServerMessageKind {
    StopWatch= "stopWatch",
    Watch = "watch",
    Init = "init",
    StartModel = "startModel",
    StopModel = "stopModel"
}

export const enum ClientMessageKind {
    ClientStatus = "clientStatus",
    ViewUpdate = "viewUpdate",
    Init = "init"
}

export const enum ResourceType {
    JSONResult = "jsonResult",
    Runtime = "runtime"
}

export interface Model {
    client: Client
    servers: Server[]
    variables: Variable[]
    results?: Results
}

export interface Client {
    topology: Topology
    endpoints: Endpoint[]
    suites: Suite[]
}

export interface Topology {
    local: Local
}

export interface Local {
    parallel: number
    repeat: number
}

export interface Endpoint {
    name: string
    host: string
    port: number
    protocol?: string
    prefix?: string
    isdefault? :boolean
}

export interface Suite {
    kind?: string;
    name: string
    steps: Step[]
    variables?: Variable[]
}

export interface Step {
    name: string
    endpoint: string
    path: string
    method: string
    body?: string
    register?: string
    headers: Header[]
    assertions: Assertion[]
    kind?: string
}

export interface Header {
    name: string
    value: string
}

export interface Assertion {
    name: string
    body: string
}

export interface Server {
    name: string
    host: string
    port: number
    handlers: Handler[]
}

export interface Handler {
    path: string
    method: string
    response: Response
}

export interface Response {
    body: string
}

export interface Variable {
    name: string
    value: string
}

export interface Results {
    format: string
    filename: string
}

export interface ResultSet {
    creationTime: Date
    name: string
    model: Model
    results: TestResult[]
}
export interface TestResult {
    startTime: Date
    endTime: Date,
    clientId: string
    stepName: string
    statusCode: number
    assertions: AssertionResult[]
}
export interface AssertionResult {
    name: string
    passed: boolean
}

export const http_methods = ["GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH"];

//Start the websocket
const wsprotocol = location.protocol === "http:" ? "ws:" : "wss:";
const ws = new WebSocket(wsprotocol + "//" + location.host);
const handlers = new Map<ClientMessageKind,MessageHandler>();
const views = new Map();

ws.onopen = function () {
    console.log("Websocket Opened.");
    /**
     * @type {ServerMessage}
     */
    let init: ServerMessage = { kind: ServerMessageKind.Init };
    ws.send(JSON.stringify(init));
    ws.onmessage = onmessage;
}

/**
 * Generic handler for incoming websocket messages.
 * @param {MessageEvent<string>} event 
 */
function onmessage(event: MessageEvent<string>) {
    console.log("Received message: " + event.data);
    const msg: ClientMessage = JSON.parse(event.data);
    const kind = msg.kind;
    if (handlers.has(kind)) {
        //execute the handler if there is one.

        handlers.get(kind)!(msg);
    }
}

const api = {

    /**
     * Start the client and server defined in a model.
     * @param {*} model The model to start
     */
    startModel(model: Model) {
        console.log("Starting Model");
        /** @type {ServerMessage} */
        let msg: ServerMessage = {
            kind: ServerMessageKind.StartModel,
            data: { model }
        };
        sendWhenReady(msg);
    },

    stopModel() {
        console.log("Stopping");
        /** @type {ServerMessage} */
        let msg: ServerMessage = {
            kind: ServerMessageKind.StopModel,
            data: {}
        };
        sendWhenReady(msg);
    },

    /**
     * Registers a handler for a message kind.
     * @param {Function} handler 
     * @param {string} kind 
     */
    registerHandler(kind: ClientMessageKind, handler: MessageHandler) {
        handlers.set(kind, handler);
    },
    watch(resourceType: ResourceType, resourceInstance: string, handler: MessageHandler) {
        console.log(`Watching ${resourceType}/${resourceInstance}`);
        let msg: ServerMessage = {
            kind: ServerMessageKind.Watch,
            resourceType,
            resourceInstance,
            data: {}
        };
        this.registerView(resourceType, resourceInstance, handler);
        sendWhenReady(msg);
    },
    stopWatch(resourceType: ResourceType, resourceInstance: string) {
        console.log(`Stop Watching ${resourceType}/${resourceInstance}`);
        let msg: ServerMessage = {
            kind: ServerMessageKind.StopWatch,
            resourceType,
            resourceInstance,
            data: {}
        };
        this.unregisterView(resourceType, resourceInstance);
        sendWhenReady(msg);
    },
    registerView(resourceType, resourceInstance, handler: MessageHandler) {
        views.set(resourceType + "/" + resourceInstance, handler);
    },
    unregisterView(resourceType, resourceInstance) {
        views.delete(resourceType + "/" + resourceInstance);
    }
}

api.registerHandler(ClientMessageKind.ClientStatus, (msg) => {
    console.log("Received clientStatus message: " + msg);
})

api.registerHandler(ClientMessageKind.ViewUpdate,
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

function sendWhenReady(msg: ServerMessage) {
    let json: string = JSON.stringify(msg);
    if (ws.readyState == ws.OPEN) {
        ws.send(json);
    } else {
        ws.addEventListener("open", (event) => { ws.send(json) });

    }
}

export default api;

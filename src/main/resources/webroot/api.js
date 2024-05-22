var ws;

/**
 * Starts the client with the given parameters;
 * @param {*} step The step to execute.
 * @param {*} variables The variables to replace.
 * @param {number} repeat How many times to repeat.
 * @param {number} parallel Number of parallel clients.
 */
export function startClient(step,variables,repeat,parallel ) {
    console.log("Starting client");
    let data = {
        command: "execute",
        data: {
            step,variables,repeat,parallel
        }
    };
    ws.send(JSON.stringify(data));
}

export function setWebSocket(websocket) {
    ws = websocket;
}

/**
 * Returns the Websocket used to send and receive data.
 * @returns The unique Websocket.
 */
export function getWebSocket() {
    return ws;
}

function execute(step,variables,repeat,parallel ) {
}
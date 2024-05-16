const ws = new WebSocket("ws://localhost:8081");
ws.onopen = function() {
    console.log("Opened");
    ws.send("This is the first message.");
}

export function startClient() {
    //Execute different portions based on what's selected.
}

function execute(step,variables,repeat,parallel ) {

}
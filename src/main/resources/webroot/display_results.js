var jsonResult;

function load_results() {

    var table = document.getElementById("table");
    jsonResult.results.forEach((stepResult) =>{
        
        let tr = document.createElement("tr");
        
        let suite_td = document.createElement("td");
        suite_td.innerText="My Suite"
        tr.appendChild(suite_td);
        
        let step_td = document.createElement("td");
        step_td.innerText = stepResult.stepName
        tr.appendChild(step_td);
        
        let assertion_td = document.createElement("td");
        tr.appendChild(assertion_td);
        
        let passed_td = document.createElement("td");
        tr.appendChild(passed_td);
        
        let failed_td = document.createElement("td");
        tr.appendChild(failed_td);

        table.appendChild(tr);

    });
    
}

function loadFile() {
    var input, file, fr;

    if (typeof window.FileReader !== 'function') {
        alert("The file API isn't supported on this browser yet.");
        return;
    }

    input = document.getElementById('fileinput');
    if (!input) {
        alert("Um, couldn't find the fileinput element.");
    }
    else if (!input.files) {
        alert("This browser doesn't seem to support the `files` property of file inputs.");
    }
    else if (!input.files[0]) {
        alert("Please select a file before clicking 'Load'");
    }
    else {
        file = input.files[0];
        fr = new FileReader();
        fr.onload = receivedText;
        fr.readAsText(file);
    }

    function receivedText(e) {
        let lines = e.target.result;
        jsonResult = JSON.parse(lines);
        load_results()
    }
}

function loadModelFile() {
    let input = document.getElementById('modelFileInput');
    let file = input.files[0];
    let fr = new FileReader();
    fr.onload = receivedText;
    fr.readAsText(file)
    function receivedText(e) {
        let lines = e.target.result;
        model = JSON.parse(lines);
        renderModel()
    }
}



function renderModel() {
    //Render Suites
    let suitesDiv = document.getElementById("suites");

    if(model.client != null) {
        model.client.suites.forEach((suite) =>{
            let suiteEl = renderSuite(suite);
            suitesDiv.appendChild(suiteEl);
        });
    }
}

function renderSuite(suite) {
    let suiteDiv = document.createElement("div");
    let nameElement = document.createElement("h4");

    nameElement.innerText = suite.name;

    suiteDiv.appendChild(nameElement);

    suite.steps.forEach((step) => {
        suiteDiv.appendChild(renderStep(step));

    });
    return suiteDiv;
}

function renderStep(step) {
    let stepElement = document.createElement("div");
    stepElement.className = "step";
    stepElement.innerText= step.method + " " + step.path;
    return stepElement;
}

//Event bus
var eb;

window.onload = function onLoad() {
    eb = new EventBus('http://localhost:8081/eventbus');
    eb.onopen = () => {
        console.log('Registering handler');
        // set a handler to receive a message
        eb.registerHandler('some-address', (error, message) => {
          console.log('received a message: ' + JSON.stringify(message));
        });
       
        // send a message
        eb.send('some-address', {name: 'tim', age: 587});
       
       }
}

function startClient() {
    eb.send('some-address', {name: 'tim', age: 587});
}

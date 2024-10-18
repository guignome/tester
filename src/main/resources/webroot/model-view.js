import StepView from './step-view.js'
import EndpointsView from './endpoints-view.js';
import ServersView from './servers-view.js';
import VariablesView from './variables-view.js';
import SuitesView from './suites-view.js';

import api from './api.js'
/**
 * @ty
 */
export default {
    setup() { },
    created() {
        api.watch("runtime","main",
            /**
             * 
             * @param {import('./api.js').ClientMessage} msg 
             */
            (msg) => {
                this.running = msg.data.running;
                this.runningReportName = msg.data.reportName;
                if(msg.data.running){
                    this.$emit('newReport',this.runningReportName);
                }
            });
        api.registerHandler("init", (msg) => {
            this.model = initModel(msg.data);
        })
    },
    components: { StepView, EndpointsView,ServersView,VariablesView,SuitesView },
    data() {
        return {
            model: {
                client: {
                    topology: {
                        local: {
                            parallel: 7,
                            repeat: 5
                        }
                    }
                },
                results: {format: "json", filename: "sample.json"}
            },
            /**
             * Can be Running or Stopped.
             * @type {boolean}
             */
            running: false,
            reportType: {},
            runningReportName: null,
            modelUri: null
        };
    },
    computed: {
        runtimeButtonMessage() {
            return this.running ? "Stop" : "Start";
        },
        runtimeStatusMessage() {
            return this.running ? "Running: " + this.runningReportName : "Stopped";
        }
    },
    emits: ['newReport','selected'],
    methods: {
        readFile() {
            console.log("Reading file");
            let fr = new FileReader();
            fr.onload = (res) => {
                let content = res.target.result;
                this.model = initModel(jsyaml.load(content));
            }
            fr.readAsText(this.$refs.doc.files[0]);
        },
        startStop() {
            if (this.running) {
                this.running = false;
                api.stopModel();
            } else {
                this.running = true;
                if(this.model?.results?.filename === undefined || this.model?.results?.filename == null) {
                    this.model.results.filename = "report.json"
                }
                api.startModel(this.model);
            }
        },
        selected(element) {
            this.$emit('selected',element);
        },
    },
    mounted() { },
    props: [],
    template: `
<div>
    <fieldset>
    <legend>Load Model</legend>
        <input type="file" ref="doc" @change="readFile()" />
        <label for="modelUri">Load from uri:</label>
        <input type="uri" id="modelUri" name="modelUri" v-model="modelUri"/>
        <button @click="load()">Load</button>
        
    </fieldset>
    
    <SuitesView :suites="model?.client?.suites"
        @selected="selected"/>

    <EndpointsView :endpoints="model?.client?.endpoints"
      @selected="selected"/>

    <ServersView 
      :servers="model.servers"
      @selected="selected" />

    <VariablesView
      :variables="model.variables"
      @selected="selected" />

  <fieldset>
    <legend>Runtime</legend>
    <div style="display: grid; grid-template-columns:auto auto auto auto" class="form">
        <label for="repeat">Repeat</label>
        <input type="number" min="1" id="repeat" name="repeat" v-model="model.client.topology.local.repeat"/>
        
        <label for="parallel">Parallel</label>
        <input type="number" min="1" id="parallel" name="parallel" v-model="model.client.topology.local.parallel"/>
        
        <label for="report-type">Report type:</label>
        <select name="report-type" id="report-type" v-model="model.results.format">
            <option value="csv">CSV</option>
            <option value="tps">TPS</option>
            <option value="json">JSON</option>
            <option value="summary">Summary</option>
        </select>
    
        <label for="report-name">Report name:</label>
        <input id="report-name" v-model=model.results.filename>
    </div>
        <button @click="startStop()">{{runtimeButtonMessage}}</button>
        <b>Status:</b> <span>{{runtimeStatusMessage}}</span>
   
  </fieldset>
</div>`
}

function initModel(m) {
    if (m.client == null) {
        m.client = {
            topology: {
                local: {
                    parallel: 1,
                    repeat: 1
                }
            },
            endpoints: [],
            suites: []
        };
    } else if (m.client.topology === undefined) {
        m.client.topology = {
            local: {
                parallel: 1,
                repeat: 1
            }
        }
    }
    if (m.variables === undefined) {
        m.variables = [];
    }
    if(m.results === undefined) {
        m.results = {format:"json", filename:"test.json"}
    }
    return m;
}

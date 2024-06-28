import StepView from './step-view.js'
import api from './api.js'

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
    },
    components: { StepView },
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
            runningReportName: null
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
    emits: ['newReport'],
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
                api.startModel(this.model);
            }
        }
    },
    mounted() { },
    props: [],
    template: `
<div class="">
            <fieldset>
            <legend>Load Model</legend>
                <input type="file" ref="doc" @change="readFile()" />
            </fieldset>
    <fieldset>
    <legend>Client</legend>
    <fieldset>
    <legend>Suites</legend>
    <div v-for="suite in model?.client?.suites">
        <h4 class="">{{suite.name}}</h4>
        <div v-for="step in suite?.steps">
            <StepView :step="step"/>
        </div>
    </div>
    </fieldset>

    <fieldset>
    <legend>Endpoints</legend>
    <div id="endpoints">
        <table v-if="model?.client?.endpoints" class="">
            <tr>
                <th>Name</th>
                <th>Protocol</th>
                <th>Host</th>
                <th>Port</th>
                <th>Prefix</th>
                <th>Default</th>
            </tr>
            <tr v-for="endpoint in model.client.endpoints">
                <td>{{endpoint.name}}</td>
                <td>{{endpoint.protocol}}</td>
                <td>{{endpoint.host}}</td>
                <td>{{endpoint.port}}</td>
                <td>{{endpoint.prefix}}</td>
                <td>{{endpoint.isdefault}}</td>
            </tr>
        </table>
    </div>
    </fieldset>
    </fieldset>

<fieldset>
    <legend>Servers</legend>
    <div v-for="server in model.servers">
    <h3 class="">{{server.name}} ({{server.host}}:{{server.port}})</h3>
    <div v-for="handler in server.handlers">
      <b>{{handler.method}}</b> {{handler.path}} -> {{handler.response}} 
    </div>
  
  
  </div>
</fieldset>
<fieldset>
    <legend>Variables</legend>
  <div id="variables">
      <table id="variables-table" class="w3-table-all">
          <tr>
              <th>Name</th>
              <th>Value</th>
          </tr>
          <tr v-for="variable in model.variables">
              <td contenteditable="true">{{variable.name}}</td>
              <td contenteditable="true">{{variable.value}}</td>
          </tr>
      </table>
  </div>
  </fieldset>

  <fieldset>
    <legend>Runtime</legend>
  <div class="">
    <label for="repeat">Repeat</label>
    <input type="number" min="1" id="repeat" name="repeat" v-model="model.client.topology.local.repeat"/>
    <label for="parallel">Parallel</label>
    <input type="number" min="1" id="parallel" name="parallel" v-model="model.client.topology.local.parallel"/>
    <div>
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

    <div>
        <button @click="startStop()">{{runtimeButtonMessage}}</button>
        <b>Status:</b> <span>{{runtimeStatusMessage}}</span>
    </div>
    </div>
    </fieldset>
</div>`
}

function initModel(m) {
    if (m.client === undefined) {
        m.client = {
            topology: {
                local: {
                    parallel: 1,
                    repeat: 1
                }
            }
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

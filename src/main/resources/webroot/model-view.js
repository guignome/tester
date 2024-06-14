import StepView from './step-view.js'
import api from './api.js'

export default {
    setup() { },
    created() {
        api.watch("runtime",(data)=> {
            this.running = data.running
            this.runningReportName= data.reportName});
     },
    components: {StepView},
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
                }
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
            return this.running? "Stop" : "Start";
        },
        runtimeStatusMessage() {
            return this.running? "Running" : "Stopped";
        }
    },
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
            if(this.running) {
                this.running = false;
                api.stopClient();
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
  <h1 class="w3-container w3-theme-l1">Model</h1>
            <fieldset>
                <div>Model file</div>
                <input type="file" ref="doc" @change="readFile()" />
            </fieldset>
  <h2 class="w3-theme-d1">Client</h2>
    <h3 class="w3-theme-d2">Suites</h3>
    <div v-for="suite in model?.client?.suites">
        <h4 class="w3-theme-d3">{{suite.name}}</h4>
        <div v-for="step in suite?.steps">
            <StepView :step="step"/>
        </div>
    </div>

    <h3 class="w3-theme-d2">Endpoints</h3>
    <div id="endpoints">
    <table v-if="model?.client?.endpoints" class="w3-table-all">
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

<h2 class="w3-theme-d1">Servers</h2>
  <div v-for="server in model.servers">
    <h3 class="w3-theme-d2">{{server.name}} ({{server.host}}:{{server.port}})</h3>
    <div v-for="handler in server.handlers">
      <b>{{handler.method}}</b> {{handler.path}} -> {{handler.response}} 
    </div>
  
  
  </div>

<h2 class="w3-theme-d1">Variables</h2>
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

  <h2 class="w3-theme-d1">Runtime</h2>
  <div class="w3-panel">
    <label for="repeat">Repeat</label>
    <input type="number" min="1" id="repeat" name="repeat" v-model="model.client.topology.local.repeat"/>
    <label for="parallel">Parallel</label>
    <input type="number" min="1" id="parallel" name="parallel" v-model="model.client.topology.local.parallel"/>
    <div>
        <label for="report-type">Report type:</label>

        <select name="report-type" id="report-type" v-model="reportType">
        <option value="csv">CSV</option>
        <option value="tps">TPS</option>
        <option value="json">JSON</option>
        <option value="summary">Summary</option>
        </select>
    </div>

    <div>
        <button @click="startStop()">{{runtimeButtonMessage}}</button>
        <b>Status:</b> <span>{{runtimeStatusMessage}}</span>
    </div>
    </div>
</div>`
    

}

function initModel(m) {
    if(m.client === undefined) {
        m.client =  {
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
    if(m.variables == undefined) {
        m.variables = [];
    }
    return m;
}

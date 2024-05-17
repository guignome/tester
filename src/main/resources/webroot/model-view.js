import { ref } from 'vue'
import StepView from './step-view.js'

export default {
    setup() { },
    created() { },
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
            }
        };
    },
    computed: {},
    methods: {
        readFile() {
            console.log("Reading file");
            let fr = new FileReader();
            fr.onload = (res) => {
                let content = res.target.result;
                this.model = initModel(jsyaml.load(content));
            }
            fr.readAsText(this.$refs.doc.files[0]);
        }
    },
    mounted() { },
    props: [],
    template: `
<div>
  <h1>Model</h1>
            <fieldset>
                <div>Model file</div>
                <input type="file" ref="doc" @change="readFile()" />
            </fieldset>
  <h2>Client</h2>
    <h3>Suites</h3>
    <div v-for="suite in model?.client?.suites">
        <h4>{{suite.name}}</h4>
        <div v-for="step in suite?.steps">
            <StepView :step="step"/>
        </div>
    </div>

  <h3>Endpoints</h3>
  <div id="endpoints">
    <table v-if="model?.client?.endpoints">
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

  <label for="repeat">Repeat</label>
  <input type="number" min="1" id="repeat" name="repeat" v-model="model.client.topology.local.repeat"/>
  <label for="parallel">Parallel</label>
  <input type="number" min="1" id="parallel" name="parallel" v-model="model.client.topology.local.parallel"/>
  <div>
      <button onclick="startClient()">Start</button>
  </div>

<h2>Servers</h2>
  <div id="servers"></div>

<h2>Variables</h2>
  <div id="variables">
      <table id="variables-table">
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
    return m;
}

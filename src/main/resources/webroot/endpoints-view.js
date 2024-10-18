export default {
    setup() { },
    created() { },
    emits: ['selected'],
    components: {},
    data() {
        return {}
    },
    computed: {

    },
    methods: {
        selected(endpoint) {
           endpoint.kind='endpoint';
            this.$emit('selected',endpoint);
        },
        addEndpoint(){
            this.endpoints.push({name: "myapp", protocol:"http",host:"localhost",
                port: 1234, prefix:"",isdefault: false
            });
        },
        deleteEndpoint(){

        }
    },
    mounted() { },
    props: ['endpoints'],
    template: `
    <fieldset>
        <legend>Endpoints</legend>
        <div id="endpoints">
            <table v-if="endpoints">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Protocol</th>
                    <th>Host</th>
                    <th>Port</th>
                    <th>Prefix</th>
                    <th>Default</th>
                </tr>
                </thead>
                <tbody>
                <tr 
                  v-for="endpoint in endpoints"
                  @click="selected(endpoint)"
                  class="selectable"
                  >
                    <td>{{endpoint.name}}</td>
                    <td>{{endpoint.protocol}}</td>
                    <td>{{endpoint.host}}</td>
                    <td>{{endpoint.port}}</td>
                    <td>{{endpoint.prefix}}</td>
                    <td>{{endpoint.isdefault}}</td>
                </tr>
                </tbody>
            </table>
            <div style="float: right;">
                <button @click="addEndpoint">Add Endpoint</button>
                <button @click="deleteEndpoint">Delete Endpoint</button>
            </div>
        </div>
    </fieldset>`
}

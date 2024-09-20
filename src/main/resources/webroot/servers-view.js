import ServerHandlerView from "./server-handler-view.js";
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
    components: {ServerHandlerView},
    methods: {
        selected(server) {
           server.kind='server';
            this.$emit('selected',server);
        },
        handlerselected(handler) {
             this.$emit('selected',handler);
         },
         addHandler(server){
            server.handlers.push({method:"GET",path:"/",response:"This is a body"});
         },
         deleteHandler(){

         },
         addServer(){
            this.servers.push({name:"name",host:"localhost", port:1234,handlers:[]});
         },
         deleteServer(){

         }
    },
    mounted() { },
    props: ['servers'],
    template: `
    <fieldset>
        <legend>Servers</legend>
        <div v-for="server in servers">
            <h3 class="">{{server.name}} ({{server.host}}:{{server.port}})</h3>
            <div v-for="handler in server.handlers" class="selectable">
                <ServerHandlerView
                    :serverhandler="handler"
                    @selected="handlerselected"
                />
            </div>
            <div style="float: right;">
                <button @click="addHandler(server)">Add Handler</button>
                <button @click="deleteHandler">Delete Handler</button>
            </div>
        </div>
        <div style="float: right;">
            <button @click="addServer">Add Server</button>
            <button @click="deleteServer">Delete Server</button>
        </div>
    </fieldset>`
}

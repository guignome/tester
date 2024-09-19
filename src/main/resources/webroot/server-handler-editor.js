export default {
    data() {
        return {}
    },
    computed: {
        serverhandler(){
            return this.initialElement;
        }
    },
    methods: {
        save() {

        },
        cancel() {
            
        }
        
    },
    mounted() { },
    props: ['initialElement'],
    template: `
        <fieldset>
            <legend>Server Handler Editor</legend>
            <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
               <label for="method">Method:</label>
                <select id="method" v-model="serverhandler.method">
                    <option>GET</option>
                    <option>HEAD</option>
                    <option>POST</option>
                    <option>PUT</option>
                    <option>DELETE</option>
                    <option>CONNECT</option>
                    <option>OPTIONS</option>
                    <option>TRACE</option>
                    <option>PATCH</option>
                </select>
            

                <label for="path" >Path:</label>
                <input id="path" v-model="serverhandler.path"/>
                 
                <label for="response" >Response:</label>
                <input id="response" v-model="serverhandler.response"/>

            <div style="float: right;">
                <button>Cancel</button>
                <button>Save</button>
                <button>Delete</button>
            </div>
        </div>
        </fieldset>
    `
}

export default {
    created() {
        this.generated= this.serverhandler.response.body == null;
    },
    data() {
        return {
            _generated: false
        }
    },
    computed: {
        serverhandler(){
            return this.initialElement;
        },
        generated: {
            get(){
                return this._generated;
            },
            set(newValue) {
                this._generated = newValue;
                if(newValue) {
                    this.serverhandler.response.body = null;
                }
            }
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
                 
                <p>Response:</p>
                <div>
                    <form>
                        <input name="generated" type="radio" id="generated" :value="true" v-model="generated" />
                        <label for="generated">Generated</label>

                        <input name="generated" type="radio" id="static" :value="false" v-model="generated" />
                        <label for="static">Static</label>
                    </form>
                </div>

                <input v-if="generated" type="number" min="0" v-model="serverhandler.response.generatedBodySize"/>
                <textarea v-else v-model="serverhandler.response.body"/>

            <div style="float: right;">
                <button>Cancel</button>
                <button>Save</button>
                <button>Delete</button>
            </div>
        </div>
        </fieldset>
    `
}

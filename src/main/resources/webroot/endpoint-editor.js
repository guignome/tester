export default {
    data() {
        return {}
    },
    computed: {
        endpoint(){
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
        <legend>Endpoint Editor</legend>
        <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
                <label for="name">Name:</label>
                <input id="name" v-model="endpoint.name" />

                <label for="protocol" >Protocol:</label>
                <select id="protocol" v-model="endpoint.protocol">
                    <option value="http">HTTP</option>
                    <option value="https">HTTPS</option>
                </select>

                <label for="host" >Host:</label>
                <input id="host" v-model="endpoint.host"/>

                <label for="port" >Port:</label>
                <input id="port" v-model="endpoint.port" type="number" min="0" max="65535"/>

                <label for="prefix" >Prefix:</label>
                <input id="prefix" v-model="endpoint.prefix"/>

                <label for="isdefault" >Is Default:</label>
                <input id="isdefault" v-model="endpoint.isdefault" type="checkbox"/>

            <div style="float: right;">
                <button>Cancel</button>
                <button>Save</button>
                <button>Delete</button>
            </div>
        </div>
        </fieldset>
    `
}

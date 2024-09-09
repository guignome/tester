export default {
    setup() { },
    created() { },
    emits: [],
    components: {},
    data() {
        return {

        }
    },
    computed: {
        
        step(){
            return this.initialElement;
        }
    },
    methods: {
        save() {

        },
        cancel() {
            
        }
        
    },
    mounted() { 
    },
    props: ['initialElement'],
    template: `
    <div >
    <fieldset>
    <legend>Step Editor</legend>
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
            <label for="name">Name:</label>
            <input id="name" v-model="step.name" />
        
            <label for="method">Method:</label>
            <select id="method" v-model="step.method">
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
            <input id="path" v-model="step.path"/>
            
            <label for="body" >Body:</label>
            <input id="body" v-model="step.body"/>
            
            <label for="endpoint" >Endpoint:</label>
            <input id="endpoint" v-model="step.endpoint"/>
            
            <label for="register" >Register:</label>
            <input id="register" v-model="step.register"/>

        <div style="float: right;">
            <button>Cancel</button>
            <button>Save</button>
            <button>Delete</button>
        </div>
    </div>
    </fieldset>
    </div>`
}

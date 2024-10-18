export default {
    data() {
        return {}
    },
    computed: {
        server(){
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
        <legend>Server Editor</legend>
        <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
                <label for="name">Name:</label>
                <input id="name" v-model="variable.name" />
            

                <label for="value" >Value:</label>
                <input id="value" v-model="variable.value"/>

            <div style="float: right;">
                <button>Cancel</button>
                <button>Save</button>
                <button>Delete</button>
            </div>
        </div>
        </fieldset>
    `
}

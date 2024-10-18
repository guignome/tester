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
        selected(variable) {
           variable.kind='variable';
            this.$emit('selected',variable);
        },
        addVariable() {
            this.variables.push({name: "name", value:"value"});
        },
        deleteVariable() {

        }
    },
    mounted() { },
    props: ['variables'],
    template: `
    <fieldset>
        <legend>Variables</legend>
        <div id="variables">
            <table id="variables-table" class="w3-table-all">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="variable in variables" @click='selected(variable)' class="selectable">
                        <td>{{variable.name}}</td>
                        <td>{{variable.value}}</td>
                    </tr>
                </tbody>
            </table>
            <div style="float: right;">
                <button @click="addVariable">Add Variable</button>
                <button @click="deleteVariable">Delete Variable</button>
            </div>
        </div>
    </fieldset>`
}

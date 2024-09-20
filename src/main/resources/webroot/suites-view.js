import StepView from './step-view.js'

export default {
    setup() { },
    created() { },
    emits: ['selected'],
    components: {},
    data() {
        return {}
    },
    components: {StepView},
    computed: {

    },
    methods: {
        selected(suite) {
           suite.kind='suite';
            this.$emit('selected',suite);
        },
        step_selected(step) {
            this.$emit('selected',step);
        },
        addSuite() {
            this.suites.push({name: "new Test Suite",steps: []});
        },
        deleteSuite() {

        },
        addStep(suite){
            suite.steps.push({name: "Call Server",method:"GET",path:"/abc"});
        },
        deleteStep(){

        }
    },
    mounted() { },
    props: ['suites'],
    template: `
    <fieldset>
        <legend>Client Test Suites</legend>
        <div v-for="suite in suites">
            <h4>{{suite.name}}</h4>
            <div v-for="step in suite?.steps">
                <StepView :step="step" @selected="step_selected(step)"/>
            </div>
            <div style="float: right;">
                <button @click="addStep(suite)">Add Step</button>
                <button @click="deleteStep">Delete Step</button>
            </div>
        </div>
        <div style="float: right;">
            <button @click="addSuite">Add Suite</button>
            <button @click="deleteSuite">Delete Suite</button>
        </div>
    </fieldset>`
}

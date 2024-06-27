import api from 'api'

export default {
    setup() { },
    created() {
        api.watch("jsonResult",this.result.name,(data)=> {
            console.log(`Received jsonresults update: ${data}`);
            if(data.data != null){
                this.$emit('updateResult',data.data);
            }
        });
        
    },
    data() {
        return {}}
    ,
    computed: {},
    methods: {
        
    },
    mounted() { },
    props: ['result'],
    emits: ['updateResult'],
    template: `<div class="jsonResultsView">
    <div>
    <h2>Test summary</h2>
    <h3>{{result.name}}</h3>
    <p>Creation Time: {{result.creationTime}}</p>
    <p>Tests Passed:</p> 
    </div>
    <table id="resultset" class="">
        <tr>
            <th>Start Time</th>
            <th>End Time</th>
            <th>Client ID</th>
            <th>StepName</th>
            <th>Assertions #</th>
        </tr>
        <tr v-for="res in result.results">
            <td>{{res.startTime}}</td>
            <td>{{res.endTime}}</td>
            <td>{{res.clientId}}</td>
            <td>{{res.StepName}}</td>
            <td>{{res.assertions.length}}</td>
        </tr>
    </table>
    </div>`
}
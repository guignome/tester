import api from 'api'

export default {
    setup() { },
    created() {
        api.watch("jsonresults",(data)=> {
            this.running = data.running
            this.runningReportName= data.reportName});
     },
    data() {
        return {}}
    ,
    computed: {},
    methods: {
        update(data){
            this.results = data;
        }
    },
    mounted() { },
    props: ['result'],
    template: `<div class="jsonResultsView">
    <div>
    <h2>Test summary</h2>
    Start Time: {{}}
    Tests Passed: 
    </div>
    <table id="resultset" class="w3-table-all">
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

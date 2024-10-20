import api from 'api'

const options = {
    hour: "numeric",
    minute: "numeric",
    second: "numeric",
    fractionalSecondDigits: 3
  };
const timeFormat = new Intl.DateTimeFormat("en-US",options);

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
    unmounted() {
        api.stopWatch("jsonResult",this.result.name);
    },

    data() {
        return {}}
    ,
    computed: {},
    methods: {
        formatStartTime(start) {
            const time = new Date(start);
            return timeFormat.format(time);
        },
        formatDuration(start,end) {
            return new Date(end) - new Date(start);
        },
        /**
         * Create the text for assertions
         * @param {Array} assertions 
         */
        formatAssertions(assertions) {
            const size = assertions.length;
            const passed = assertions.filter(a => a.passed).length;
            return `${passed}/${size}`;
        },
        /**
         * Create css style for an array of assertions
         * @param {Array} assertions 
         */
        styleAssertions(assertions) {
            const failed = assertions.filter(a => !a.passed).length;
            if(failed > 0) {
                return {backgroundColor: 'red'};
            } else {
                return {backgroundColor: 'green'};
            }
        }
    },
    mounted() { },
    props: ['result'],
    emits: ['updateResult'],
    template: `<div class="jsonResultsView">
    <div>
    <p><b>Name:</b> {{result.name}}</p>
    <p><b>Creation Time:</b> {{result.creationTime}}</p>
    <p><b>Total Duration:</b> {{formatDuration(result?.summary?.startTime,result?.summary?.endTime)}}</p> 
    <p><b>Total Size:</b> {{result?.summary?.size}}</p> 

    </div>
    <table id="resultset" class="">
        <thead>  
            <tr>
                <th>Client ID</th>
                <th>Step Name</th>
                <th>Start Time</th>
                <th>Duration (ms)</th>
                <th>HTTP Status</th>
                <th>Assertions (Passed/Total)</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="res in result.results">
                <td>{{res.clientId}}</td>
                <td>{{res.stepName}}</td>
                <td>{{formatStartTime(res.startTime)}}</td>
                <td>{{formatDuration(res.startTime,res.endTime)}}</td>
                <td>{{res.statusCode}}</td>
                <td :style="styleAssertions(res.assertions)">{{formatAssertions(res.assertions)}}</td>
            </tr>
        </tbody>
    </table>
    </div>`
}
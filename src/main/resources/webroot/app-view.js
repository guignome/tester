import modelView from './model-view.js'
import resultsView from './results-view.js'
import editor from './editor.js'
import api from './api.js'

const sample = {
    creationTime: "2024-05-20T13:23:34.561506982",
    name: "sample.json",
    model: {
      variables: [],
      client: {
        topology: {
          local: {
            parallel: 10,
            repeat: 5
          }
        },
        endpoints: [
          {
            protocol: "http",
            name: "From url",
            host: "localhost",
            port: 8080,
            prefix: "",
            isdefault: true
          }
        ],
        suites: [
          {
            name: "suite1",
            variables: [],
            steps: [
              {
                method: "GET",
                path: "",
                body: "",
                endpoint: "default",
                name: "Step 0",
                headers: [],
                assertions: [
                  {
                    name: "HTTP Return Code is OK",
                    body: "{result.statusCode().equals(200)}"
                  }
                ]
              }
            ]
          }
        ]
      },
      servers: [],
      defaultServer: null
    },
    results: [
      {
        startTime: "2024-05-20T13:23:34.64364324",
        endTime: "2024-05-20T13:23:34.717745706",
        clientId: "0",
        stepName: "Step 0",
        assertions: [
          {
            name: "HTTP Return Code is OK",
            passed: true
          }
        ]
      },
      {
        startTime: "2024-05-20T13:23:34.656842349",
        endTime: "2024-05-20T13:23:34.728000445",
        clientId: "1",
        stepName: "Step 0",
        assertions: [
          {
            name: "HTTP Return Code is OK",
            passed: true
          }
        ]
      },
     

      {
        startTime: "2024-05-20T13:23:34.732250499",
        endTime: "2024-05-20T13:23:34.741898381",
        clientId: "4",
        stepName: "Step 0",
        assertions: [
          {
            name: "HTTP Return Code is OK",
            passed: true
          }
        ]
      }]}
const sample1 = {...sample};
const sample2 = {...sample};
sample1.name= "sample1.json";
sample2.name= "sample2.json";


export default {
    setup() { },
    created() {
        
     },
    components: {modelView,resultsView, editor},
    data() {
        return {
          resultsets: [sample1,sample2],
          activeResultName: sample1.name,
          selection:{ name: "Not initialized"}
        }
    },
    computed: {},
    methods: {
      selected(element) {
        this.selection = element;
      },
      onNewReport(report) {
        this.resultsets.push({name: report});
      },
      closeResult(r) {
        let foundindex;
        this.resultsets.forEach((element,index,arr) => {
          if(r.name === element.name) {
            foundindex = index;
          }
        });
        this.resultsets.splice(foundindex,1);
        if(this.resultsets.length >0) {
          this.activeResultName=this.resultsets[0].name;
        }
      },
      updateResult(newResult) {
        let foundindex;
        this.resultsets.forEach((element,index,arr) => {
          if(newResult.name === element.name) {
            foundindex = index;
          }
        });
        this.resultsets[foundindex]=newResult;
      }
    },
    mounted() { },
    props: [],
    template: `
<div class="header">
    <h1>Tester</h1>
</div>
<div class="menu">
    <modelView @newReport="onNewReport" @selected="selected"></modelView>
</div>
<div class="main" >
    <resultsView 
      @updateResult="newResult=>this.updateResult(newResult)" 
      @closeResult="r=>this.closeResult(r)"
      :resultsets="this.resultsets" 
      v-model:activeResultName=activeResultName>
    </resultsView>
</div>
<div class="footer">
  <editor :initialElement="selection" />
</div>
`
}

import modelView from './model-view.js'
import resultsView from './results-view.js'
import api from './api.js'

const sample = {
    creationTime: "2024-05-20T13:23:34.561506982",
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

export default {
    setup() { },
    created() {
        
     },
    components: {modelView,resultsView},
    data() {
        return {resultsets: [sample,sample]}
    },
    computed: {},
    methods: {
        
    },
    mounted() { },
    props: [],
    template: `
<div class="w3-row w3-theme-l3">
    <h1>Tester</h1>
</div>
<div class="w3-col l3 w3-theme-l2">
    <modelView></modelView>
</div>
<div class="w3-col l9 w3-theme-l2" >
    <resultsView :resultsets></resultsView>
</div>
`
}

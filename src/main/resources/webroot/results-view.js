import { ref } from 'vue'
import {getWebSocket} from 'api'

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
  data() {
    return {resultsets:[sample]}
  },
  template: `<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

                <fieldset>
                    <h2>Load JSON Result file</h2>
                    <input type='file' id='fileinput'>
                    <input type='button' id='btnLoad' value='Load' onclick='loadFile();'>
                </fieldset>
            </form>
            <div class="w3-bar w3-black">
                <button v-for="resultset in resultsets" class="w3-bar-item w3-button" onclick="openCity('London')">{{resultset.creationTime}}</button>
            </div>
            <table id="resultset" class="w3-table-all">
                <tr>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Client ID</th>
                    <th>StepName</th>
                    <th>Assertions #</th>
                </tr>
                <tr v-for="result in resultsets[0].results">
                    <td>{{result.startTime}}</td>
                    <td>{{result.endTime}}</td>
                    <td>{{result.clientId}}</td>
                    <td>{{result.StepName}}</td>
                    <td>{{result.assertions.length}}</td>
                </tr>
            </table>
</div>`
}


const ws = getWebSocket();
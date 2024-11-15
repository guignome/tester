import type { Model, ResultSet } from "./api";

let sampleModel: Model = {
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
  results: {filename: "results.json", format: "JSON"}
};

let sampleResult: ResultSet = {
  creationTime: new Date("2024-05-20T13:23:34.561506982"),
  name: "sample.json",
  model: sampleModel,
  results: [
    {
      startTime: new Date("2024-05-20T13:23:34.64364324"),
      endTime: new Date("2024-05-20T13:23:34.717745706"),
      clientId: "0",
      stepName: "Step 0",
      statusCode: 200,
      assertions: [
        {
          name: "HTTP Return Code is OK",
          passed: true
        }
      ]
    },
    {
      startTime: new Date("2024-05-20T13:23:34.656842349"),
      endTime: new Date("2024-05-20T13:23:34.728000445"),
      clientId: "1",
      stepName: "Step 0",
      statusCode: 200,
      assertions: [
        {
          name: "HTTP Return Code is OK",
          passed: true
        },
        {
          name: "Length is not null",
          passed: true
        }
      ]
    },


    {
      startTime: new Date("2024-05-20T13:23:34.732250499"),
      endTime: new Date("2024-05-20T13:23:34.741898381"),
      clientId: "4",
      stepName: "Step 0",
      statusCode: 0,
      assertions: [
        {
          name: "HTTP Return Code is OK",
          passed: false
        }
      ]
    }],
};
export default {sampleResult,sampleModel};
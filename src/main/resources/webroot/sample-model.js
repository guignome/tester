export default  {
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
    },
    results: [
      {
        startTime: "2024-05-20T13:23:34.64364324",
        endTime: "2024-05-20T13:23:34.717745706",
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
        startTime: "2024-05-20T13:23:34.656842349",
        endTime: "2024-05-20T13:23:34.728000445",
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
        startTime: "2024-05-20T13:23:34.732250499",
        endTime: "2024-05-20T13:23:34.741898381",
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
      "summary": {
        "statusCodesCount": {
            "200": 12,
            "0": 1
        },
        "size": 3,
        "startTime": "2024-09-20T20:49:59.647505752-04:00",
        "endTime": "2024-09-20T20:49:59.710551747-04:00"
    }}
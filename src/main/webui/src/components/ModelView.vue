<script lang="ts">
import { defineComponent } from 'vue'
import api, { ClientMessageKind, ResourceType, type ClientMessage, type Model } from '@/api';
import * as jsyaml from 'js-yaml';
import samples from '@/sample-model';

function initModel(m: Model) {
  if (m.client == null) {
    m.client = {
      topology: {
        local: {
          parallel: 1,
          repeat: 1
        }
      },
      endpoints: [],
      suites: []
    };
  } else if (m.client.topology === undefined) {
    m.client.topology = {
      local: {
        parallel: 1,
        repeat: 1
      }
    }
  } else if (m.client.endpoints === undefined) {
    m.client.endpoints = [];
  }
  if (m.variables === undefined) {
    m.variables = [];
  }
  if (m.results === undefined) {
    m.results = { format: "json", filename: "test.json" }
  }
  return m;
}

export default defineComponent({
  created() {
    api.watch(ResourceType.Runtime, "main",
      (msg: ClientMessage) => {
        this.running = msg.data.running;
        this.runningReportName = msg.data.reportName;
        if (msg.data.running) {
          this.$emit('newReport', this.runningReportName);
        }
      });
    api.registerHandler(ClientMessageKind.Init, (msg: ClientMessage) => {
      this.model = initModel(msg.data);
    })
  },
  data() {
    return {
      model: samples.sampleModel as Model,
      /**
       * Can be Running or Stopped.
       * @type {boolean}
       */
      running: false,
      reportType: {},
      runningReportName: null,
      modelUri: null,
      reportTypes: ["TPS","JSON","CSV"]
    };
  },
  computed: {
    runtimeButtonMessage() {
      return this.running ? "Stop" : "Start";
    },
    runtimeStatusMessage() {
      return this.running ? "Running: " + this.runningReportName : "Stopped";
    }
  },
  emits: ['newReport', 'selected'],
  methods: {
    readFile() {
      console.log("Reading file");
      let fr = new FileReader();
      fr.onload = (res) => {
        let content = res.target?.result as string;
        this.model = initModel(jsyaml.load(content) as Model);
      }
      fr.readAsText((this.$refs.doc as any).files[0]);
    },
    startStop() {
      if (this.running) {
        this.running = false;
        api.stopModel();
      } else {
        this.running = true;
        if (this.model?.results?.filename === undefined || this.model?.results?.filename == null) {
          this.model.results!.filename = "report.json"
        }
        api.startModel(this.model);
      }
    },
    selected(element) {
      this.$emit('selected', element);
    },
    load() {

    }
  },
})
</script>

<!-- Template -->
<template>
  <div>
    <Fieldset legend="Load Model" :toggleable="true">
      <input type="file" ref="doc" @change="readFile()" />
      <label for="modelUri">Load from uri:</label>
      <input type="uri" id="modelUri" name="modelUri" v-model="modelUri" />
      <Button @click="load()"label="Load"/>

    </Fieldset>

    <SuitesView :suites="model?.client?.suites" @selected="selected" />

    <EndpointsView :endpoints="model?.client?.endpoints" @selected="selected" />

    <ServersView :servers="model.servers" @selected="selected" />

    <VariablesView :variables="model.variables" @selected="selected" />

    <Fieldset legend="Runtime" :toggleable="true">

      <div style="display: grid; grid-template-columns:auto auto auto auto" class="form">
        <label for="repeat">Repeat</label>
        <InputNumber :min="1" id="repeat" name="repeat" v-model="model.client.topology.local.repeat" showButtons/>

        <label for="parallel">Parallel</label>
        <InputNumber :min="1" id="parallel" name="parallel" v-model="model.client.topology.local.parallel" showButtons/>

        <label for="report-type">Report type:</label>
        <Select v-model="model.results!.format" :options="reportTypes" placeholder="Report type" />

        <label for="report-name">Report name:</label>
        <InputText id="report-name" v-model="model.results!.filename"/>
      </div>
      <Button @click="startStop()" :label="runtimeButtonMessage"/>
      <b>Status:</b> <span>{{ runtimeStatusMessage }}</span>

    </Fieldset>
  </div>
</template>


<!-- Style -->
<style scoped></style>

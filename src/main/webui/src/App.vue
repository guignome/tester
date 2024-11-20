<script lang="ts">
import { defineComponent } from 'vue'
import { SelectionKind, type ResultSet } from './api';


export default defineComponent({
  data() {
    return {
      resultsets: [] as ResultSet[],
      activeResultName: "",
      selection: { name: "Not initialized" },
      kind: SelectionKind.step
    }
  },
  computed: {},
  methods: {
    selected(element,kind: SelectionKind) {
      this.selection = element;
      this.kind= kind;
    },
    /**
     * Method called when a new report is created.
     * @param {String} report The name of the new report
     */
    onNewReport(report: string) {
      this.resultsets.push({ name: report } as ResultSet);
      this.activeResultName = report;
    },
    closeResult(r: { name: string; }) {
      let foundindex;
      this.resultsets.forEach((element, index, arr) => {
        if (r.name === element.name) {
          foundindex = index;
        }
      });
      this.resultsets.splice(foundindex, 1);
      if (this.resultsets.length > 0) {
        this.activeResultName = this.resultsets[0].name;
      }
    },
    updateResult(newResult) {
      let foundindex;
      this.resultsets.forEach((element, index, arr) => {
        if (newResult.name === element.name) {
          foundindex = index;
        }
      });
      this.resultsets[foundindex] = newResult;
    },
    loadResult(f: string) {
      this.resultsets.push({name: f} as ResultSet);
    }
  }
})
</script>

<template>
  <div>
    <h1>Tester</h1>
  </div>
  <Splitter>
    <SplitterPanel :size="40">
      <ModelView @newReport="onNewReport" @selected="selected"></ModelView>
    </SplitterPanel>
    <SplitterPanel :size="60">
      <Splitter layout="vertical">
        <SplitterPanel :size="70">
          <ResultsView @updateResult="updateResult" 
            @closeResult="closeResult"
            @loadResult="loadResult"
            :resultsets="resultsets" 
            v-model:activeResultName=activeResultName>
          </ResultsView>
        </SplitterPanel>
        <SplitterPanel :size="30">
          <editor :initialElement="selection" :kind="kind" />
        </SplitterPanel>
      </Splitter>
    </SplitterPanel>
  </Splitter>

</template>

<style scoped>

</style>

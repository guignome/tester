<script lang="ts">
import { defineComponent } from 'vue'
import samples from './sample-model'
import type { ResultSet } from './api';

const sample1: ResultSet = { ...samples.sampleResult };
const sample2: ResultSet = { ...samples.sampleResult };
sample1.name = "sample1.json";
sample2.name = "sample2.json";

export default defineComponent({
  data() {
    return {
      resultsets: [sample1, sample2],
      activeResultName: sample1.name,
      selection: { name: "Not initialized" }
    }
  },
  computed: {},
  methods: {
    selected(element) {
      this.selection = element;
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
    }
  }
})
</script>

<template>
  <div>
    <h1>Tester</h1>
  </div>
  <Splitter>
    <SplitterPanel :size="50">
      <ModelView @newReport="onNewReport" @selected="selected"></ModelView>
    </SplitterPanel>
    <SplitterPanel :size="50">
      <Splitter layout="vertical">
        <SplitterPanel :size="50">
          <ResultsView @updateResult="newResult => updateResult(newResult)" @closeResult="r => closeResult(r)"
            :resultsets="resultsets" v-model:activeResultName=activeResultName>
          </ResultsView>
        </SplitterPanel>
        <SplitterPanel :size="50">
          <editor :initialElement="selection" />
        </SplitterPanel>
      </Splitter>
    </SplitterPanel>
  </Splitter>

</template>

<style scoped>

</style>

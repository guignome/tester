<script lang="ts">
import type { Results, ResultSet } from '@/api';
import api, { ResourceType } from '@/api';
import { defineComponent, type InputHTMLAttributes, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
      resultFiles: [] as String[],
      loadingFile: ""
    }
  },
  created() {
    api.watch(ResourceType.ResultFiles, "main", (msg) => {
      console.log(`Received ResultFiles update: ${msg}`);
      if (msg.data != null) {
        //data should be an array of String[] wil filenames
        this.resultFiles = msg.data;
      }
    });
  },
  unmounted() {
    api.stopWatch(ResourceType.ResultFiles, "main");
  },
  props: {
    resultsets: { type: Object as PropType<ResultSet[]>, required: true, },
    activeResultName: { type: String, required: true, }
  },
  emits: {
    updateResult: (r: Results) => true,
    closeResult: (r: Results) => true,
    loadResult: (f: String) => true,
  },
  methods: {
    readFile() {
      console.log("Reading file");
      let fr = new FileReader();
      fr.onload = (res) => {
        const content = res.target!.result as string;
        this.resultsets.push(JSON.parse(content));
      }
      // @ts-ignore
      fr.readAsText(this.$refs.doc as InputHTMLAttributes.files[0]);
    },
    closeTab(resultset) {
      this.$emit('closeResult', resultset);
    },
    load() {
      this.$emit('loadResult', this.loadingFile);
    }
  }
})
</script>

<!-- Template -->
<template>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

    <Fieldset legend="Load JSON Result file" :toggleable="true">
      <input type="file" ref="doc" @change="readFile()" />
      <Select v-model="loadingFile" :options="resultFiles" filter placeholder="Result File" />
      <Button label="Load" @click="load" />
    </Fieldset>
  </form>
  <Tabs :value="resultsets[0]?.name">
    <TabList>
      <Tab v-for="(resultset, index) in resultsets" :value="resultset.name" :key="resultset.name">
        {{ resultset.name }} <span @click="closeTab(resultset)">&times;</span>
      </Tab>

    </TabList>
    <TabPanels>
      <TabPanel v-for="r in resultsets" :value="r.name" :key="r.name">
        <JSONResultView @updateResult="(newResult: Results) => $emit('updateResult', newResult)" :result="r"
          :key="r.name">
        </JSONResultView>
      </TabPanel>
    </TabPanels>
  </Tabs>

</template>


<!-- Style -->
<style scoped></style>

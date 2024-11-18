<script lang="ts">
import type { Results, ResultSet } from '@/api';
import { defineComponent, type InputHTMLAttributes, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
    }
  },
  props: {
    resultsets: {type: Object as PropType<ResultSet[]>, required: true, },
    activeResultName: {type: String, required: true, }
  },
  emits: {updateResult:(r: Results) =>{},
    closeResult: (r: Results) =>{},
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
    }
  }
})
</script>

<!-- Template -->
<template>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

    <Fieldset legend="Load JSON Result file" :toggleable="true">
      <input type="file" ref="doc" @change="readFile()" />
    </Fieldset>
  </form>
  <Tabs :value="resultsets[0].name">
    <TabList>
      <Tab v-for="(resultset, index) in resultsets" :value="resultset.name" :key="resultset.name"> 
        {{ resultset.name }} <span @click="closeTab(resultset)">&times;</span>
      </Tab>

    </TabList>
    <TabPanels>
      <TabPanel v-for="r in resultsets" :value="r.name" :key="r.name">
        <JSONResultView @updateResult="(newResult: Results) => $emit('updateResult', newResult)" :result="r" :key="r.name">
        </JSONResultView>
      </TabPanel>
    </TabPanels>
  </Tabs>

</template>


<!-- Style -->
<style scoped></style>

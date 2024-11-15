<script lang="ts">
import { defineComponent, type InputHTMLAttributes } from 'vue'

export default defineComponent({
  data() {
    return {
    }
  },
  props: ['resultsets', 'activeResultName'],
  emits: ['updateResult', 'update:activeResultName', 'closeResult', 'update:resultsets'],
  methods: {
    readFile() {
      console.log("Reading file");
      let fr = new FileReader();
      fr.onload = (res) => {
        const content = res.target!.result as string;
        this.resultsets.push(JSON.parse(content));
        this.$emit('update:resultsets', this.resultsets);
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
        <JSONResultView @updateResult="newResult => $emit('updateResult', newResult)" :result="r" :key="r.name">
        </JSONResultView>
      </TabPanel>
    </TabPanels>
  </Tabs>

</template>


<!-- Style -->
<style scoped></style>

<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { SelectionKind, type Suite } from '../api';
import type { TreeNode } from 'primevue/treenode';

export default defineComponent({
  data() {
    return {
      selectedKey: undefined,
    }
  },

  computed: {
    nodes() {
      let tree: TreeNode[] = [];
      for (var suite of this.suites) {
        let node: TreeNode = { key: suite.name, label: suite.name ,children:[], type: "suite", data: suite};
        for(var step of suite.steps) {
          node.children?.push({key: step.name, label: step.name, type: "step", data: step});
        }
        tree.push(node);
      }
      return tree;
    }
  },
  props: {
    suites: {
      type: Object as PropType<Array<Suite>>,
      required: true
    }
  },
  emits: {
    selected: (s: Suite,kind: SelectionKind) =>true
  },
  methods: {
    addSuite() {
      if (this.suites)
        this.suites!.push({ name: "new Test Suite", steps: [] } as Suite);
    },
    deleteSuite() {
    },
    addStep(suite) {
      suite.steps.push({ name: "Call Server", method: "GET", path: "/abc" });
    },
    deleteStep() {
    },
    onNodeSelect(node: TreeNode) {
      if(node.type == "suite") {
        let suite = node.data;
        this.$emit('selected', suite, SelectionKind.suite);
      } else if (node.type == "step") {
        let step = node.data;
        this.$emit('selected', step,SelectionKind.step);
      }
    }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Client Test Suites" :toggleable="true">
    <div v-for="node in nodes">
    <Tree :value="[node]" 
      selectionMode="single" 
      v-model:selectionKeys="selectedKey" 
      @nodeSelect="onNodeSelect">
      <template #suite="slotProps">
        <b>{{ slotProps.node.label }}</b>
      </template>
      <template #step="slotProps">
        <StepView :step="slotProps.node.data" />
      </template>
      
    </Tree>
    <ButtonGroup style="float: right;">
        <Button @click="addStep(node.data)" v-tooltip="'Add Step'" icon="pi pi-plus" size="small" />
        <Button @click="deleteStep" v-tooltip="'Delete Step'" icon="pi pi-minus" size="small" />
      </ButtonGroup>
  </div>


    <ButtonGroup style="float: right;">
      <Button @click="addSuite" v-tooltip="'Add Suite'" icon="pi pi-plus" size="small" />
      <Button @click="deleteSuite" v-tooltip="'Delete Suite'" icon="pi pi-minus" size="small" />
    </ButtonGroup>
  </Fieldset>
</template>


<!-- Style -->
<style scoped></style>

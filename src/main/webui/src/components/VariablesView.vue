<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import type { Variable } from '@/api';

export default defineComponent({
  data() {
    return {
      selectedVariable: {}
    }
  },
  props: {
    variables: {type: Object as PropType<Variable[]>, required: true}
  },
  emits: ['selected'],
  methods: {
    selected(variable) {
           variable.kind='variable';
            this.$emit('selected',variable);
        },
        addVariable() {
            this.variables.push({name: "name", value:"value"});
        },
        deleteVariable() {

        },
        onRowSelect(event) {
            //@ts-ignore
            this.selectedVariable.kind='variable';
            this.$emit('selected',this.selectedVariable);
        }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Variables" :toggleable="true">
    
    <DataTable :value="variables" 
              selectionMode="single"
              v-model:selection="selectedVariable"
              @rowSelect="onRowSelect">
      <Column field="name" header="Name"></Column>
      <Column field="value" header="Value"></Column>
    
    </DataTable>
        
            <ButtonGroup style="float: right;">
                <Button @click="addVariable" v-tooltip="'Add Variable'" icon="pi pi-plus" size="small"/>
                <Button @click="deleteVariable" v-tooltip="'Delete Variable'" icon="pi pi-minus" size="small"/>
              </ButtonGroup>
    </Fieldset>
</template>


<!-- Style -->
<style scoped>

</style>

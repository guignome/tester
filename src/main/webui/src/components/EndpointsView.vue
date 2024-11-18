<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import type { Endpoint } from '@/api';

export default defineComponent({
  data() {
    return {
      selectedEndpoint: {} as Endpoint
    }
  },
  props: {
    endpoints: { type: Object as PropType<Endpoint[]>, required: true }
  },
  emits: {
    selected: (e: Endpoint) =>{}
  },

  methods: {
    selected(endpoint: Endpoint) {
      endpoint.kind = 'endpoint';
      this.$emit('selected', endpoint);
    },
    addEndpoint() {
      this.endpoints.push({
        name: "myapp", protocol: "http", host: "localhost",
        port: 1234, prefix: "", isdefault: false
      });
    },
    deleteEndpoint() {

    },
    onRowSelect(event) {
      //@ts-ignore
      this.selectedEndpoint.kind = 'endpoint';
      this.$emit('selected', this.selectedEndpoint);
    }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Endpoints" :toggleable="true">
    <DataTable :value="endpoints" selectionMode="single" v-model:selection="selectedEndpoint" @rowSelect="onRowSelect">
      <Column field="name" header="Name"></Column>
      <Column field="protocol" header="Protocol"></Column>
      <Column field="host" header="Host"></Column>
      <Column field="port" header="Port"></Column>
      <Column field="prefix" header="Prefix"></Column>
      <Column field="isdefault" header="Default" dataType="boolean"></Column>

    </DataTable>

    <ButtonGroup style="float: right;" size="small">
      <Button @click="addEndpoint" v-tooltip="'Add Endpoint'" icon="pi pi-plus" size="small" />
      <Button @click="deleteEndpoint" v-tooltip="'Delete Endpoint'" icon="pi pi-minus" size="small" />
    </ButtonGroup>
  </Fieldset>
</template>


<!-- Style -->
<style scoped></style>

<script lang="ts">
import type { Endpoint } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
      protocols: ["http", "https"]
    }
  },
  computed: {
    endpoint() {
      return this.initialElement;
    }
  },
  props: {
    initialElement: {
      type: Object as PropType<Endpoint>,
      required: true
    }
  },
  methods: {
    save() {

    },
    cancel() {

    }

  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Endpoint Editor" :toggleable="true">
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="name">Name:</label>
      <InputText id="name" v-model="endpoint.name" />

      <label for="protocol">Protocol:</label>
      <Select id="protocol" :options="protocols" v-model="endpoint.protocol" />

      <label for="host">Host:</label>
      <InputText id="host" v-model="endpoint.host" />

      <label for="port">Port:</label>
      <InputNumber id="port" v-model="endpoint.port" showButtons :min="0" :max="65535" />

      <label for="prefix">Prefix:</label>
      <InputText id="prefix" v-model="endpoint.prefix" />

      <label for="isdefault">Is Default:</label>
      <ToggleSwitch id="isdefault" v-model="endpoint.isdefault" />


      <ButtonGroup style="float: right;">
        <Button label="Save" icon="pi pi-check" />
        <Button label="Delete" icon="pi pi-trash" />
        <Button label="Cancel" icon="pi pi-times" />
      </ButtonGroup>

    </div>
  </Fieldset>
</template>


<!-- Style -->
<style scoped></style>

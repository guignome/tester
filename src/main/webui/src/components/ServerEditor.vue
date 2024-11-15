<script lang="ts">
import type { Server } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {

    }
  },
  props: {
    initialElement: {
      type: Object as PropType<Server>,
      required: true
    }
  },
  computed: {
    server() {
      return this.initialElement;
    }
  },
  methods: {
    save() {

    },
    cancel() {

    }

  },
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Server Editor" :toggleable="true">
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="name">Name:</label>
      <InputText id="name" v-model="server.name" />

      <label for="host">Host:</label>
      <InputText id="host" v-model="server.host" />

      <label for="port">Port:</label>
      <InputNumber  showButtons :min="0" :max="65535" id="port" v-model="server.port" />

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

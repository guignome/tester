<script lang="ts">
import type { Step } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
      http_methods:["GET","HEAD","POST","PUT","DELETE","CONNECT","OPTIONS","TRACE","PATCH"]
    }
  },
  computed: {

    step() {
      return this.initialElement;
    }
  },
  methods: {
    save() {

    },
    cancel() {

    }

  },
  mounted() { },
  props: {
    initialElement: {
      type: Object as PropType<Step>,
      required: true
    }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Step Editor" :toggleable="true">
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="name">Name:</label>
      <InputText id="name" v-model="step.name" />

      <label for="method">Method:</label>
      <Select id="method" :options="http_methods" v-model="step.method" />

      <label for="path">Path:</label>
      <InputText id="path" v-model="step.path" />

      <label for="body">Body:</label>
      <Textarea id="body" v-model="step.body" />

      <label for="endpoint">Endpoint:</label>
      <InputText id="endpoint" v-model="step.endpoint" />

      <label for="register">Register:</label>
      <InputText id="register" v-model="step.register" />

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

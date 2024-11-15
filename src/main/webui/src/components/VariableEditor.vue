<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  data() {
    return {}
  },
  computed: {
    variable() {
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
  props: ['initialElement'],
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Variable Editor" :toggleable="true">
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="name">Name:</label>
      <InputText id="name" v-model="variable.name" />


      <label for="value">Value:</label>
      <InputText id="value" v-model="variable.value" />

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

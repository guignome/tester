<script lang="ts">
import type { Suite } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {}
  },
  computed: {
    suite() {
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
      type: Object as PropType<Suite>,
      required: true
    }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Suite Editor" :toggleable="true">

    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="name">Name:</label>
      <InputText id="name" v-model="suite.name" />

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

<script lang="ts">
import { SelectionKind } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {

    }
  },
  methods: {
    selected() {

    },

  },
  props: {
    initialElement: {
      type: Object,
      required: true
    },
    kind: {type: String, required: true}
  }
})
</script>

<!-- Template -->
<template>
  <div>
    <StepEditor v-if="kind === 'step'" :initialElement="initialElement" />
    <SuiteEditor v-else-if="kind === 'suite'" :initialElement="initialElement" />
    <EndpointEditor v-else-if="kind === 'endpoint'" :initialElement="initialElement" />
    <ServerEditor v-else-if="kind === 'server'" :initialElement="initialElement" />
    <ServerHandlerEditor v-else-if="kind === 'handler'" :initialElement="initialElement" />
    <VariableEditor v-else-if="kind === 'variable'" :initialElement="initialElement" />
  </div>
</template>


<!-- Style -->
<style scoped></style>

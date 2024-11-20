<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { SelectionKind, type Step } from '@/api';

export default defineComponent({
  data() {
    return {

    }
  },
  props: {
    step: { type: Object as PropType<Step>, required: true }
  },
  emits: {
    selected: (s: Step,kind: SelectionKind) =>true
  },
  methods: {
    selected() {
      this.$emit('selected', this.step, SelectionKind.step);
    },

  }
})
</script>

<!-- Template -->
<template>
  <div @click="selected" class="selectable">
    <a>{{ step.name }} (<b>{{ step.method }}</b> {{ step.path }})</a>
  </div>
</template>


<!-- Style -->
<style scoped></style>

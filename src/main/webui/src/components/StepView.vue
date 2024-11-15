<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import type { Step } from '@/api';

export default defineComponent({
  data() {
    return {
      myvar: "hi",

    }
  },
  props: {
    step: { type: Object as PropType<Step>, required: true }
  },
  emits: ['selected'],
  methods: {
    selected() {
      this.step!.kind = 'step';
      this.$emit('selected', this.step);
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

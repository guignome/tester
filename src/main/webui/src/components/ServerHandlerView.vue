<script lang="ts">
import type { Handler } from '@/api';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
      myvar: "hi",
    }
  },
  emits: {
    selected: (h: Handler) =>true
  },
  props: {
    serverhandler: {type: Object as PropType<Handler>, required: true}
  },
  methods: {
    selected() {

    },

  }
})
</script>

<!-- Template -->
<template>
  <div @click="selected" class="selectable">
    <div v-if="serverhandler?.response.body != null">
      <b>{{ serverhandler.method }}</b> {{ serverhandler.path }} -> {{ serverhandler?.response.body }}
    </div>
    <div v-else>
      <b>{{ serverhandler.method }}</b> {{ serverhandler.path }} -> Generated({{ serverhandler?.response.generatedBodySize }})
    </div>
  </div>
</template>


<!-- Style -->
<style scoped></style>

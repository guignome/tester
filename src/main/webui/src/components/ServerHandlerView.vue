<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  data() {
    return {
      myvar: "hi",

    }
  },
  emits: ['selected'],
  props: ['serverhandler'],
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

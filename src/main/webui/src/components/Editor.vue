<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  data() {
    return {
      myvar: "hi",
      
    }
  },
  methods: {
    selected() {
      
    },
    
  },
  props: ['initialElement']
})
</script>

<!-- Template -->
<template>
   <div>
        <StepEditor 
            v-if="initialElement.kind === 'step'" 
            :initialElement="initialElement"/>
        <SuiteEditor 
            v-else-if="initialElement.kind === 'suite'" 
            :initialElement="initialElement"/>
        <EndpointEditor 
            v-else-if="initialElement.kind === 'endpoint'" 
            :initialElement="initialElement"/>
        <ServerEditor 
            v-else-if="initialElement.kind === 'server'" 
            :initialElement="initialElement"/>
        <ServerHandlerEditor 
            v-else-if="initialElement.kind === 'handler'" 
            :initialElement="initialElement"/>
        <VariableEditor 
            v-else-if="initialElement.kind === 'variable'" 
            :initialElement="initialElement"/>
    </div>
</template>


<!-- Style -->
<style scoped>

</style>

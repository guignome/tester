<script lang="ts">
import { defineComponent, type PropType } from 'vue'
import { http_methods, type Handler } from '@/api';

export default defineComponent({
  created() {
    this.generated = this.serverhandler.response.body == null;
  },
  data() {
    return {
      _generated: false,
      http_methods: http_methods
    }
  },
  computed: {
    serverhandler() {
      return this.initialElement;
    },
    generated: {
      get() {
        return this._generated;
      },
      set(newValue) {
        this._generated = newValue;
        if (newValue) {
          this.serverhandler.response.body = null;
        }
      }
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
      type: Object as PropType<Handler>,
      required: true
    }
  },
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Server Handler Editor" :toggleable="true">
    <div style="display: grid; grid-template-columns: auto auto auto auto;" class="form">
      <label for="method">Method:</label>
      <Select id="method" :options="http_methods" v-model="serverhandler.method" />

      <label for="path">Path:</label>
      <InputText id="path" v-model="serverhandler.path" />

      <p>Response:</p>
      <label for="switch1">Generated:</label>
      <ToggleSwitch inputId="switch1" v-model="generated" />

      <input v-if="generated" type="number" min="0" v-model="serverhandler.response.generatedBodySize" />
      <Textarea v-else v-model="serverhandler.response.body" />

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

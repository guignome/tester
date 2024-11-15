<script lang="ts">
import type { Server } from '@/api';
import type { TreeNode } from 'primevue/treenode';
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  data() {
    return {
      selectedKey: undefined,
    }
  },
  emits: ['selected'],
  computed: {
    nodes() {
      let tree: TreeNode[] = [];
      for (var server of this?.servers) {
        let node: TreeNode = { key: server.name, label: server.name, children: [], type: "server", data: server };
        for (var handler of server.handlers) {
          node.children?.push({ key: handler.path, label: handler.path, type: "handler", data: handler });
        }
        tree.push(node);
      }
      return tree;
    }
  },
  props: {
    servers: {
      type: Object as PropType<Server[]>,
      required: true
    }
  },

  methods: {
    selected(server) {
      server.kind = 'server';
      this.$emit('selected', server);
    },
    handlerselected(handler) {
      this.$emit('selected', handler);
    },
    addHandler(server: Server) {
      server.handlers.push({ method: "GET", path: "/", response: { body: "Body" } });
    },
    deleteHandler() {

    },
    addServer() {
      this.servers.push({ name: "name", host: "localhost", port: 1234, handlers: [] });
    },
    deleteServer() {

    },
    onNodeSelect(node: TreeNode) {
      if (node.type == "server") {
        let server = node.data;
        server.kind = 'server';
        this.$emit('selected', server);
      } else if (node.type == "handler") {
        let handler = node.data;
        handler.kind = 'handler';
        this.$emit('selected', handler);
      }
    }
  }
})
</script>

<!-- Template -->
<template>
  <Fieldset legend="Servers" :toggleable="true">

    <div v-for="node in nodes">
      <Tree :value="[node]" 
        selectionMode="single" 
        v-model:selectionKeys="selectedKey" 
        @nodeSelect="onNodeSelect">
        <template #server="slotProps">
          <b>{{ slotProps.node.data.name }} ({{ slotProps.node.data.host }}:{{ slotProps.node.data.port }})</b>
        </template>
        <template #handler="slotProps">
          <ServerHandlerView :serverhandler="slotProps.node.data" @selected="handlerselected" />
        </template>

      </Tree>
      <ButtonGroup style="float: right;">

        <Button @click="addHandler(node.data)" v-tooltip="'Add Handler'" icon="pi pi-plus" size="small"/>
        <Button @click="deleteHandler" v-tooltip="'Delete Handler'" icon="pi pi-minus" size="small"/>
      </ButtonGroup>
    </div>

    <ButtonGroup style="float: right;">
      <Button @click="addServer" v-tooltip="'Add Server'" icon="pi pi-plus" size="small"/>
      <Button @click="deleteServer" v-tooltip="'Delete Server'" icon="pi pi-minus" size="small"/>
    </ButtonGroup>
  </Fieldset>
</template>


<!-- Style -->
<style scoped></style>

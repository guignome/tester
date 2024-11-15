import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import PrimeVue from 'primevue/config';

import Aura from '@primevue/themes/aura';
import Lara from '@primevue/themes/lara';
import Material from '@primevue/themes/material'
import Nora from '@primevue/themes/nora'

import Button from "primevue/button";
import ButtonGroup from 'primevue/buttongroup';
import ModelView from './components/ModelView.vue';
import ResultsView from './components/ResultsView.vue';
import Editor from './components/Editor.vue';
import EndpointEditor from './components/EndpointEditor.vue';
import EndpointsView from './components/EndpointsView.vue';
import JsonResultView from './components/JsonResultView.vue';
import ServerEditor from './components/ServerEditor.vue';
import ServerHandlerEditor from './components/ServerHandlerEditor.vue';
import ServerHandlerView from './components/ServerHandlerView.vue';
import ServersView from './components/ServersView.vue';
import StepEditor from './components/StepEditor.vue';
import StepView from './components/StepView.vue';
import SuiteEditor from './components/SuiteEditor.vue';
import SuitesView from './components/SuitesView.vue';
import VariableEditor from './components/VariableEditor.vue';
import VariablesView from './components/VariablesView.vue';
import Fieldset from 'primevue/fieldset';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Select from 'primevue/select';
import InputNumber from 'primevue/inputnumber';
import InputText from 'primevue/inputtext';
import ToggleSwitch from 'primevue/toggleswitch';
import Textarea from 'primevue/textarea';

import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import TabPanels from 'primevue/tabpanels';
import TabPanel from 'primevue/tabpanel';
import Tree from 'primevue/tree';
import Splitter from 'primevue/splitter';
import SplitterPanel from 'primevue/splitterpanel';
import Tooltip from 'primevue/tooltip';






const app = createApp(App);
app.use(PrimeVue, {
    theme: {
        preset: Lara
    }
});

app.component("Button", Button);
app.component("ButtonGroup", ButtonGroup);

app.component("Fieldset",Fieldset);

app.component("Editor",Editor);
app.component("EndpointEditor",EndpointEditor);
app.component("EndpointsView",EndpointsView);
app.component("JSONResultView",JsonResultView);
app.component("ModelView",ModelView);
app.component("ResultsView",ResultsView);
app.component("ServerEditor",ServerEditor);
app.component("ServerHandlerEditor",ServerHandlerEditor);
app.component("ServerHandlerView",ServerHandlerView);
app.component("ServersView",ServersView);
app.component("StepEditor",StepEditor);
app.component("StepView",StepView);
app.component("SuiteEditor",SuiteEditor);
app.component("SuitesView",SuitesView);
app.component("VariableEditor",VariableEditor);
app.component("VariablesView",VariablesView);
app.component("DataTable",DataTable);
app.component("Column",Column);
app.component("Select",Select);
app.component("InputNumber",InputNumber);
app.component("InputText",InputText);
app.component("ToggleSwitch",ToggleSwitch);
app.component("Textarea",Textarea);
app.component("Tabs",Tabs);
app.component("TabList",TabList);
app.component("Tab",Tab);
app.component("TabPanels",TabPanels);
app.component("TabPanel",TabPanel);
app.component("Tree",Tree);
app.component("Splitter",Splitter);
app.component("SplitterPanel",SplitterPanel);

app.directive('tooltip', Tooltip);

app.mount('#app')

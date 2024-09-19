import StepEditor from "./step-editor.js";
import SuiteEditor from "./suite-editor.js";
import EndpointEditor from "./endpoint-editor.js";
import ServerEditor from "./server-editor.js";
import ServerHandlerEditor from "./server-handler-editor.js";
import VariableEditor from "./variable-editor.js";

export default {
    setup() { },
    created() { },
    emits: [],
    components: {},
    data() {
        return {

        }
    },
    components: {StepEditor,SuiteEditor,EndpointEditor,ServerEditor,ServerHandlerEditor,VariableEditor},
    mounted() { 
    },
    props: ['initialElement'],
    template: `
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
            v-else-if="initialElement.kind === 'serverhandler'" 
            :initialElement="initialElement"/>
        <VariableEditor 
            v-else-if="initialElement.kind === 'variable'" 
            :initialElement="initialElement"/>
    </div>`
}

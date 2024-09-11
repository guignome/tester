import StepEditor from "./step-editor.js";
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
    components: {StepEditor,VariableEditor},
    mounted() { 
    },
    props: ['initialElement'],
    template: `
    <div>
        <StepEditor 
            v-if="initialElement.kind === 'step'" 
            :initialElement="initialElement"/>
        <VariableEditor 
            v-else-if="initialElement.kind === 'variable'" 
            :initialElement="initialElement"/>
    </div>`
}

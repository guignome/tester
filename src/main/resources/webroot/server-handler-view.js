export default {
    setup() { },
    created() { },
    emits: ['selected'],
    components: {},
    data() {
        return {}
    },
    computed: {

    },
    methods: {
        selected() {
            this.serverhandler.kind='serverhandler';
            this.$emit('selected',this.serverhandler);
        }
    },
    mounted() { },
    props: ['serverhandler'],
    template: `
    <div @click="selected" class="selectable">
        <div v-if="serverhandler?.response.body != null">
            <b>{{serverhandler.method}}</b> {{serverhandler.path}} -> {{serverhandler?.response.body}} 
        </div>
        <div v-else>
            <b>{{serverhandler.method}}</b> {{serverhandler.path}} -> Generated({{serverhandler?.response.generatedBodySize}}) 
        </div>
    </div>`
}

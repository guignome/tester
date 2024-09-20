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
        <b>{{serverhandler.method}}</b> {{serverhandler.path}} -> {{serverhandler.response}} 
    </div>`
}
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
            this.step.kind='step';
            this.$emit('selected',this.step);
        }
    },
    mounted() { },
    props: ['step'],
    template: `
    <div @click="selected">
        <a>{{step.name}} (<b>{{step.method}}</b> {{step.path}})</a>
    </div>`
}

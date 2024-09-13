import modelView from './model-view.js'
import resultsView from './results-view.js'
import editor from './editor.js'
import api from './api.js'
import sampleModel from './sample-model.js'

const sample1 = { ...sampleModel };
const sample2 = { ...sampleModel };
sample1.name = "sample1.json";
sample2.name = "sample2.json";


export default {
  setup() { },
  created() {

  },
  components: { modelView, resultsView, editor },
  data() {
    return {
      resultsets: [sample1, sample2],
      activeResultName: sample1.name,
      selection: { name: "Not initialized" }
    }
  },
  computed: {},
  methods: {
    selected(element) {
      this.selection = element;
    },
    /**
     * Method called when a new report is created.
     * @param {String} report The name of the new report
     */
    onNewReport(report) {
      this.resultsets.push({ name: report });
      this.activeResultName = report;
    },
    closeResult(r) {
      let foundindex;
      this.resultsets.forEach((element, index, arr) => {
        if (r.name === element.name) {
          foundindex = index;
        }
      });
      this.resultsets.splice(foundindex, 1);
      if (this.resultsets.length > 0) {
        this.activeResultName = this.resultsets[0].name;
      }
    },
    updateResult(newResult) {
      let foundindex;
      this.resultsets.forEach((element, index, arr) => {
        if (newResult.name === element.name) {
          foundindex = index;
        }
      });
      this.resultsets[foundindex] = newResult;
    }
  },
  mounted() { },
  props: [],
  template: `
<div class="header">
    <h1>Tester</h1>
</div>
<div class="menu">
    <modelView @newReport="onNewReport" @selected="selected"></modelView>
</div>
<div class="main" >
    <resultsView 
      @updateResult="newResult=>this.updateResult(newResult)" 
      @closeResult="r=>this.closeResult(r)"
      :resultsets="this.resultsets" 
      v-model:activeResultName=activeResultName>
    </resultsView>
</div>
<div class="footer">
  <editor :initialElement="selection" />
</div>
`
}

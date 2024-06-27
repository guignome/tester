import { ref } from 'vue'
import api from 'api'
import jsonResultsView from './json-results-view.js'


/**
 * 
 */
export default {
  data() {
    return {}
  },
  components: {jsonResultsView},
  methods: {
    readFile() {
      console.log("Reading file");
      let fr = new FileReader();
      fr.onload = (res) => {
          let content = res.target.result;
          this.resultsets.push(JSON.parse(content));
          this.$emit('update:resultsets',this.resultsets);
      }
      fr.readAsText(this.$refs.doc.files[0]);
    },
    closeTab(resultset){
      this.$emit('closeResult',resultset);
    }
  },
  props: ['resultsets','activeResultName'],
  emits: ['updateResult','update:activeResultName','closeResult'],

  template: `
<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

    <fieldset>
      <legend>Load JSON Result file</legend>
      <input type="file" ref="doc" @change="readFile()" />
    </fieldset>
  </form>

  <div class="">
    <button v-for="resultset in resultsets" class="resultset"
      @click="$emit('update:activeResultName', resultset.name)">{{resultset.name}} <span @click="closeTab(resultset)">&times;</span></button>
  </div>
  <jsonResultsView @updateResult="newResult=>this.$emit('updateResult',newResult)" v-for="r in resultsets" :result="r" v-show="r.name === activeResultName"></jsonResultsView>
</div>`
}


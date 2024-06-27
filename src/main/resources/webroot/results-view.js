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
  },
  props: ['resultsets','activeResultName'],
  emits: ['updateResult','update:activeResultName'],

  template: `<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

                <fieldset>
                    <h2>Load JSON Result file</h2>
                    <input type="file" ref="doc" @change="readFile()" />
                </fieldset>
            </form>
            
            <div class="w3-bar w3-black">
                <button v-for="resultset in resultsets" class="w3-bar-item w3-button resultset"
                 @click="$emit('update:activeResultName', resultset.name)">{{resultset.name}}</button>
            </div>
            <!--jsonResultsView :result="resultsets.get(activeResultName)"></jsonResultsView-->
            <jsonResultsView @updateResult="newResult=>this.$emit('updateResult',newResult)" v-for="r in resultsets" :result="r" v-show="r.name === activeResultName"></jsonResultsView>
</div>`
}


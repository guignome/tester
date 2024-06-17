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
     showResults: function(resultSet) {
      console.log("Showing result: " + resultSet.name); 
      this.activeResultName = resultSet.name;
    },
    isActive(resultset) {
      return this.activeResultName === resultset.name;
    }
  },
  props: ['resultsets','activeResultName'],
  emits: ['update:resultsets','update:activeResultName'],

  template: `<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

                <fieldset>
                    <h2>Load JSON Result file</h2>
                    <input type='file' id='fileinput'>
                    <input type='button' id='btnLoad' value='Load' onclick='loadFile();'>
                </fieldset>
            </form>
            
            <div class="w3-bar w3-black">
                <button v-for="resultset in resultsets.values()" class="w3-bar-item w3-button resultset"
                 @click="$emit('update:activeResultName', resultset.name)">{{resultset.name}}</button>
            </div>
            <jsonResultsView :result="resultsets.get(activeResultName)"></jsonResultsView>
</div>`
}


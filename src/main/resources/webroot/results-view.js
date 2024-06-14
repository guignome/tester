import { ref } from 'vue'
import api from 'api'
import jsonResultsView from './json-results-view.js'



export default {
  data() {
    return {}
  },
  components: {jsonResultsView},
  methods: {
     showResults: function(resultSet) {
      var i;
      var x = document.getElementsByClassName("jsonResultsView");
      for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";  
      }
      document.getElementById(resultSet).style.display = "block"; 
    }
  },
  props: ['resultsets'],

  template: `<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

                <fieldset>
                    <h2>Load JSON Result file</h2>
                    <input type='file' id='fileinput'>
                    <input type='button' id='btnLoad' value='Load' onclick='loadFile();'>
                </fieldset>
            </form>
            
            <div class="w3-bar w3-black">
                <button v-for="resultset in resultsets" class="w3-bar-item w3-button resultset"
                 onclick="showResults(resultset)">{{resultset.creationTime}}</button>
            </div>
            <jsonResultsView v-for="resultset in resultsets" :result="resultset"></jsonResultsView>
</div>`
}


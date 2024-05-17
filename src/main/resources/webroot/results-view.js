import { ref } from 'vue'
export default {
  setup() {
    const count = ref(0)
    return { count }
  },
  template: `<div>
  <form id="jsonFile" name="jsonFile" enctype="multipart/form-data" method="post">

                <fieldset>
                    <h2>Load JSON Result file</h2>
                    <input type='file' id='fileinput'>
                    <input type='button' id='btnLoad' value='Load' onclick='loadFile();'>
                </fieldset>
            </form>
            <table id="table">
                <tr>
                    <th>Suite</th>
                    <th>Step</th>
                    <th>Assertion</th>
                    <th>Passed #</th>
                    <th>Failed #</th>
                </tr>
            </table>
</div>`
}
<template>

  <div id="app">
    <div class="aaaa">
       <!-- <div v-html="vHtml"/> -->
    </div>

  </div>
</template>



<script>

import mammoth from 'mammoth'


  export default {
    name: "DEMO",
    data () {
      return {
       vHtml: '',
      }
    },
    created() {
      const that = this
      var blob = null;
      var xhr = new XMLHttpRequest();
      xhr.open("GET", "/api/file/download?filePath=/api/data/20210301/uid00753/交付物测试4.docx");
      xhr.responseType = "blob";//force the HTTP response, response-type header to be blob
      xhr.onload = function()
      {
          blob = xhr.response;
          var myReader = new FileReader();
          myReader.readAsArrayBuffer(blob)
          myReader.onload = function(e) {
            const buffer = e.target.result // 此时是arraybuffer类型
            mammoth.convertToHtml({ arrayBuffer: buffer }).then((result) => {
              console.log(result)
              that.exportRaw('text.html',result.value);
              that.vHtml = result.value
            }).done()
          }
      }
      xhr.send();



    },
    computed: {

    },
    methods: {
       exportRaw(name, data) {
        var urlObject = window.URL || window.webkitURL || window;
        var export_blob = new Blob([data]);
        var save_link = document.createElementNS("http://www.w3.org/1999/xhtml", "a")
        save_link.href = urlObject.createObjectURL(export_blob);
        save_link.download = name;
        this.fakeClick(save_link);
      },

     fakeClick(obj) {
        var ev = document.createEvent("MouseEvents");
        ev.initMouseEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        obj.dispatchEvent(ev);
      }
    }
  }
</script>

<style lang="scss" scoped>
  .infinite-list-wrapper {
    width: 100%;
    min-height: 40px;
    max-height: 100px;
  }
  .aaaa{

    overflow: scroll;

  }
</style>

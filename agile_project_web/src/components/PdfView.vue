<template>
    <div v-loading="loading">
       <pdf v-for="item in numPages" :key="item" :src="src" :page="item" />
    </div>
</template>

<script>
import { Loading } from 'element-ui';

import pdf from 'vue-pdf'

  import {
    _preview
  } from '@/api/fileApi';
  


  async function preview() {
    const that = this;
    this.loading = true;
    this.interval = setInterval(async () => {
      const result = await _preview({sourceFile: that.sourceSrc});
      if (result.data.previewPath !== '') {
        that.loading = false;
        clearTimeout(that.interval);
        that.interval = null;
        that.src = '/api/' + that.sourceSrc;
        that.src = pdf.createLoadingTask(that.src);
        that.src.promise.then(pdf => {
          that.numPages = pdf.numPages;
        });
      }
    },1000);
  }

  export default {
    name: 'PdfView',
    components: {
      pdf
    },
    props: {
      sourceSrc: String,
    },
    methods: {
     preview,
    },
    mounted() {
     
    },
    created() {
      this.preview();
    },
    beforeDestroy: function () {
      clearTimeout(this.interval);
      this.interval = null;
    },
    watch: {
      sourceSrc(v) {
        this.preview();
      }
    },
    data() {
      return {
        numPages: "",
        src: "",
        interval: null,
        loading: false,
      };
    }
  };
</script>

<style lang="scss">
</style>

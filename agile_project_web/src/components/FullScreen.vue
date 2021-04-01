<template>
  <div v-if="showFullScreenBtn" class="full-screen-btn-con" style="display: inline-block;">
    <i @click="handleChange"
       :class="value ? 'fa fa-arrows-alt' : 'fa fa-arrows-alt'"/>
  </div>
</template>

<script>
export default {
  name: 'FullScreen',
  computed: {
    showFullScreenBtn() {
      return window.navigator.userAgent.indexOf('MSIE') < 0;
    },
  },
  props: {
    value: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    handleFullscreen() {
      const main = document.body;
      if (this.value) {
        if (document.exitFullscreen) {
          document.exitFullscreen();
        } else if (document.mozCancelFullScreen) {
          document.mozCancelFullScreen();
        } else if (document.webkitCancelFullScreen) {
          document.webkitCancelFullScreen();
        } else if (document.msExitFullscreen) {
          document.msExitFullscreen();
        }
      } else if (main.requestFullscreen) {
        main.requestFullscreen();
      } else if (main.mozRequestFullScreen) {
        main.mozRequestFullScreen();
      } else if (main.webkitRequestFullScreen) {
        main.webkitRequestFullScreen();
      } else if (main.msRequestFullscreen) {
        main.msRequestFullscreen();
      }
    },
    handleChange() {
      console.log(111);
      this.handleFullscreen();
    },
  },
  mounted() {
    let isFullscreen = document.fullscreenElement
      || document.mozFullScreenElement
      || document.webkitFullscreenElement
      || document.fullScreen
      || document.mozFullScreen
      || document.webkitIsFullScreen;
    isFullscreen = !!isFullscreen;
    document.addEventListener('fullscreenchange', () => {
      this.$emit('input', !this.value);
      this.$emit('on-change', !this.value);
    });
    document.addEventListener('mozfullscreenchange', () => {
      this.$emit('input', !this.value);
      this.$emit('on-change', !this.value);
    });
    document.addEventListener('webkitfullscreenchange', () => {
      this.$emit('input', !this.value);
      this.$emit('on-change', !this.value);
    });
    document.addEventListener('msfullscreenchange', () => {
      this.$emit('input', !this.value);
      this.$emit('on-change', !this.value);
    });
    this.$emit('input', isFullscreen);
  },
};
</script>

<style lang="scss">
  .full-screen-btn-con {
    color: #ffffff;

    i {
      cursor: pointer;
    }
  }

</style>

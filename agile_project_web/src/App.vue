<template>
  <div id="app" style="overflow-y: hidden">
    <router-view/>
  </div>
</template>
<style lang="scss">
  .body {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
  }

  .header {
    // height: 180px;
    width: 100%;
    border-bottom: 1px #e1e1e1 solid;
    padding: 20px 20px 20px;
    box-sizing: border-box;

    /*> * {*/
    /*  line-height: 32px;*/
    /*  position: relative;*/
    /*  display: flex;*/
    /*  -webkit-user-select: none;*/
    /*  -moz-user-select: none;*/
    /*  -ms-user-select: none;*/
    /*  user-select: none;*/
    /*  width: 90%;*/
    /*}*/

    .title {
      line-height: 32px;
      font-size: 22px;
    }

    .search {
      margin-top: 30px;
      width: 450px;
      position: absolute;
      right: 30px;
    }
  }

  .el-tooltip__popper {
    max-width: 500px;
    word-break: break-all;
  }

  #app {
    height: 100%;
  }

  .el-textarea__inner {
    font-family: "Microsoft YaHei";
  }

  :focus {
    outline: none;
  }

  i {
    /*font-size: 16px;*/
  }

  .abow_dialog {
    display: flex;
    justify-content: center;
    align-items: Center;
    overflow: hidden;

    .el-dialog {
      margin: 0 auto !important;
      height: 80%;
      overflow: hidden;

      .el-dialog__body {
        position: absolute;
        left: 0;
        top: 80px;
        bottom: 0;
        right: 0;
        padding: 0;
        z-index: 1;
        overflow: auto;
      }
    }
  }

</style>

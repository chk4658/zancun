<template>
  <div>
    <div style="max-height: 350px;overflow:auto;">
      <el-tree
        ref="circleTree"
        :data="circleList"
        :props="{
              label: 'name',
              children: 'children'
            }"
        node-key="id"
        :check-strictly="true"
        :default-expanded-keys="[selectedCircle.id]"
        :default-checked-keys="[selectedCircle.id]"
        @check="handleCheck"
        show-checkbox>
      </el-tree>
    </div>
    <span slot="footer" style="margin:10px 0 15px 190px;">
      <el-button @click="cancelCircle()">取 消</el-button>
      <el-button type="primary" @click="submitCircle()">确 定</el-button>
    </span>
  </div>
</template>

<script>

import { _queryCirclesMenus } from '@/api/circleApi';

async function getCircles() {
  const result = await _queryCirclesMenus();
  if (result.code === 1200) {
    result.data.circles.forEach(c => {
      this.setDisabled(c);
     
    });
    this.circleList = result.data.circles;
    
  }
}


function setDisabled (circle) {
  if (circle.id === this.circleId) {
  this.setDisabled1(circle);
  } else {
    circle.disabled = !circle.operation.hasAddProject;
    if (circle.children !== null) {
      circle.children.forEach(_c => {
        this.setDisabled(_c)
      })
    }
  }
}

function setDisabled1(circle) {
  circle.disabled = true;
  if (circle.children !== null) {
    circle.children.forEach(_c => {
      this.setDisabled1(_c);
    })
  }
}

function submitCircle() {
  this.$emit('submitCircle', this.selectedCircle);
}

function handleCheck(a, b) {
  if (b.checkedKeys.length > 0) {
    this.$refs.circleTree.setCheckedKeys([a.id]);
    this.selectedCircle = a;
  }
}

function cancelCircle() {
  this.$emit('cancelCircle');
}

export default {
  name: 'CircleTree',
  props: {
    parentCircle: Object,
    circleId: String,
  },
  data() {
    return {
      circleList: [],
    };
  },
  computed: {},
  created() {
    this.getCircles();
    this.selectedCircle = this.parentCircle;
  },
  methods: {
    handleCheck,
    submitCircle,
    getCircles,
    cancelCircle,
    setDisabled,
    setDisabled1
  },
  watch: {
    parentCircle(val) {
      this.getCircles();
      this.selectedCircle = this.parentCircle;
    }
  },
  directives: {}
};
</script>
<style scoped>

</style>

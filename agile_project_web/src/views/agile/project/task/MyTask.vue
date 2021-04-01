<template>
	<div style="width: 100%; height: 100%; display: flex; overflow-x: hidden">
		<div class="information">
			<main-top-part>
				<template v-slot:title>
					<div style="display: flex">
						<i :class="menu.icon" class="menu-icon" style="color: #01408b; font-size: 32px">
						</i>
						{{ menu.name }}
					</div>
				</template>
				<template v-slot:search>
					<!-- <div class="btn-search">
						<el-select v-model="tableQuery.projectId" filterable placeholder="请选择项目" size="small" clearable
							@change="search" style="width: 328px; margin-left: 10px">
							<el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value">
							</el-option>
						</el-select>
					</div> -->
				</template>
				<template v-slot:button>
					<el-button-group style="margin-top: 20px">
						<el-button :type="display?'primary':'default'"
							style="border-top-left-radius: 4px;border-bottom-left-radius: 4px" size="medium"
							@click="changeRole(display)">我是责任人
						</el-button>
						<el-button :type="!display?'primary':'default'"
							style="border-top-right-radius: 4px;border-bottom-right-radius: 4px" size="medium"
							@click="changeRole(display)">我是审核人
						</el-button>
					</el-button-group>
				</template>
			</main-top-part>

			<!-- <el-row style="margin: 30px 20px 80px; overflow-y: auto">
          <el-col :span="24">
            <el-table :data="tableData" class="maxh-table">
              <el-table-column prop="project.name" label="项目名称"></el-table-column>
              <el-table-column prop="projectMilestone.name" label="里程碑名称"></el-table-column>
              <el-table-column prop="createUserDepartment.fullName" label="所在部门"></el-table-column>
              <el-table-column prop="description" label="问题描述"></el-table-column>
              <el-table-column prop="involved" label="涉及区域"></el-table-column>
              <el-table-column prop="source" label="来源"></el-table-column>
              <el-table-column prop="createUser.realName" label="创建人"></el-table-column>
              <el-table-column prop="createAt" label="创建时间"></el-table-column>
              <el-table-column label="操作" width="100">
                <template slot-scope="scope">
                  <el-button type="text" @click="openIssue(scope.row.id)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row> -->

		</div>
	</div>
</template>

<script>
	import MainTopPart from "@/components/MainTopPart";
	import { _queryOwnerProjectList } from "@/api/projectApi";
	import { _getMyTasks } from "@/api/taskApi";


	async function getMyProjects() {
		const result = await _queryOwnerProjectList({ searchName: "" });
		this.projectOptions =
			result.code === 1200
				? result.data.curUserProject.map((cp) => {
					return {
						value: cp.id,
						label: cp.name,
					};
				})
				: [];
	}

	async function getMyTasks() {
		const result = await _getMyTasks(this.tableQuery);
		console.log(result)
	}

	function changeRole(flag) {
		this.display = !flag
	}

	function search() {
		console.log('asd')
	}
	export default {
		components: {
			MainTopPart,
		},
		name: "MyTask",
		data() {
			return {
				menu: {},
				tableQuery: {
					projectId: "",
					page: 1,
					size: 15,
					total: 0,
				},

				display: true,
				tableData: [],
				projectOptions: []

			};
		},
		computed: {},
		created: function () {
			const menu = JSON.parse(localStorage.getItem("MENUS")).filter(
				(menu) => menu.code === "MY_TASK"
			);
			this.menu = menu[0];
			this.getMyTasks()
		},
		mounted() {
			this.getMyProjects()

		},
		methods: {
			getMyProjects,
			changeRole,
			search,
			getMyTasks

		},
		watch: {},
		directives: {},
	};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	>>>.el-table th {
		background-color: #eee;
	}

	.information {
		width: 100%;
		flex: 1;
		background-color: #fff;
		float: left;
		display: flex;
		flex-direction: column;
		position: relative;
		border-left: 1px solid #e1e1e1;
	}

	.page {
		position: absolute;
		bottom: 20px;
		left: 20px;
	}
</style>
<style lang="scss">
	.maxh-table {
		.cell {
			overflow: visible;
			height: 100%;
		}
	}
</style>
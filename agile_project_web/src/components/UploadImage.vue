<template>
    <div>
        <el-upload
                v-loading="this.onLoading"
                element-loading-text="Loading"
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.7)"
                ref="uploadRef"
                class="avatar-uploader"
                style="margin-left: 10px;width: 108px;height: 108px"
                action="/api/upload"
                :headers="{
                    token: token
                }"
                :auto-upload="true"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :on-progress="handleProgress"
                :on-success="handleImageSuccess">
            <img v-if="form.data.image" :src="'api/' + form.data.image"
                 class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>

    </div>
</template>

<script>
    import {compareImage} from "@/api/ImageUtil"

    export default {
        name: "UploadImage",
        // 接收父组件传过来的对象
        props: {
            // uploadFileType: Array,
            imageName: String,
        },
        data() {
            return {
                form: {
                    data: {
                        image: '',
                    },
                },
                onLoading: false,   //  文件上传状态  true：上传中
                fileNames: '',
                newFileName: '',
                token: localStorage.getItem('token'),
            };
        },
        methods: {

            /**
             * 文件上传之前的回调函数
             */
            beforeAvatarUpload(file) {
                let flag = compareImage(file);
                if (!flag) {
                    this.$confirm('上传文件必须为图片格式 ', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).catch(() => {
//                关闭上传弹窗
                    });
                }
                return flag;
            },

            handleProgress() {
                this.onLoading = true;
            },

            /**
             * 上传成功
             */
            handleImageSuccess(res) {
                if (res.code === 1200) {
                    this.onLoading = false;
                    this.newFileName = res.data.path;
                    this.$set(this.form.data, "image", res.data.path);
                    // 子组件向父组件传值
                    this.$emit('uploadFilesSucc', res.data);
                }
            },
            deleteFile() {
                if (this.newFileName !== '') {
                    his.form.data.image = '';
                    this.newFileName = '';
                }
            },
            showImage(image) {
                // 初始化显示的图片地址
                this.$set(this.form.data, "image", image.image);
            },
            clearImage() {
                // 清除显示的图片地址
                this.form.data.image = '';
            }

        },
        created() {
            this.form.data.image = this.imageName;
        },
        watch: {
           imageName(val) {
               this.form.data.image = this.imageName;
           } 
        } 
    }
    ;
</script>

<style>

    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader-icon:hover {
        border-color: #409eff;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        border: 1px dashed #d9d9d9;
        color: #8c939d;
        width: 108px;
        height: 108px;
        line-height: 108px;
        text-align: center;
    }

    .avatar {
        width: 108px;
        height: 108px;
        display: block;
    }

</style>

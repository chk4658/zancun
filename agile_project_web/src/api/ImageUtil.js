/**
 *  判断上传文件是否为 apk 文件
 * @param file
 * SUN
 */
export function compareApk(file) {
    let flag = true;
    let fileName = getLast3Str(file);
    if (fileName !== 'apk') {
        flag = false;
    }
    return flag;
}

/**
 *  判断上传文件是否为 svg或png
 * @param file
 * SUN
 */
export function compareSvgOrPng(file) {
    let flag = true;
    let fileName = getLast3Str(file);
    if (fileName !== 'svg' && fileName !== 'png'&& fileName !== 'jpg') {
        flag = false;
    }

    return flag;
}

/**
 *  判断上传文件是否为图片
 * @param file
 * SUN
 */
export function compareImage(file) {
    let flag = true;
    let fileName = getLast3Str(file);
    if (fileName !== 'svg' && fileName !== 'png' && fileName !== 'jpg') {
        flag = false;
    }
    return flag;
}

/**
 * 获取上传文件的后缀名字符串 （后缀长度均为3）
 * @param file
 * @returns {string} SUN
 */
function getLast3Str(file) {
    let fileName = file.name;
    let index = fileName.lastIndexOf('.') + 1;
    return fileName.substring(index);
}
/**
 * 复制文本到剪贴板
 * @param {string} text <true> 需要复制的文本
 * @returns {Promise<void | Error>}
 */
export default async function copyToClipboard(text) {
    try {
        if (navigator.clipboard) {
            // 使用异步方法处理 navigator.clipboard.writeText，并捕获错误
            await navigator.clipboard.writeText(text);
        } else {
            // 如果 clipboard API 不可用，使用备用方法
            const textarea = getReusableTextarea();
            textarea.value = text;
            textarea.select();
            document.execCommand('copy');
        }
    } catch (error) {
        console.error('复制到剪贴板失败:', error);
        return error;
    }
}

// 创建或获取一个可复用的 textarea 元素
let reusableTextarea = null;
function getReusableTextarea() {
    if (!reusableTextarea) {
        reusableTextarea = document.createElement('textarea');
        document.body.appendChild(reusableTextarea);
        reusableTextarea.style.position = 'fixed';
        reusableTextarea.style.top = '0'; // 移到屏幕外部，避免影响布局
        reusableTextarea.style.left = '-9999px';
        reusableTextarea.style.visibility = 'hidden'; // 使用 visibility: hidden 隐藏
    }
    return reusableTextarea;
}
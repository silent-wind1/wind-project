/**
 * 环境变量配置
 */
// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:2411/api'

// 静态资源地址
export const STATIC_BASE_URL = `${API_BASE_URL}/uploads`

// 获取部署应用的完整URL
export const getDeployUrl = (deployKey: string) => {
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态资源预览URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  return `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
}

// 获取完整的图片URL
export const getImageUrl = (relativePath: string): string => {
  if (!relativePath) return ''

  // 如果已经是完整URL，直接返回
  if (relativePath.startsWith('http://') || relativePath.startsWith('https://')) {
    return relativePath
  }

  // 拼接完整的图片URL
  return `${STATIC_BASE_URL}/${relativePath}`
}

// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 上传文件 POST /file/upload */
export async function uploadFile(
  file: File,
  fileRequest: API.LdsUploadFileRequest, // 添加元数据参数
  options?: { [key: string]: string },
) {
  const formData = new FormData()

  // 添加文件字段
  formData.append('file', file)

  // 添加元数据字段
  Object.keys(fileRequest).forEach((key: string) => {
    formData.append(key, (fileRequest as Record<string, any>)[key])
  })

  return request<API.BaseResponseString>('/file/upload', {
    method: 'POST',
    data: formData, // 使用 FormData
    ...(options || {}),
  })
}

/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /health/get */
export async function health(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.healthParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponse>('/health/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

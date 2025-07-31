declare namespace API {
  type BaseResponse = {
    code?: number
    data?: Record<string, any>
    message?: string
  }

  type healthParams = {
    name: string
  }
}

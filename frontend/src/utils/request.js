import axios from 'axios'
import { Message } from 'element-ui'

// Create axios instance
const service = axios.create({
  baseURL: '/', // Use relative path so proxy works
  timeout: 5000 // Request timeout
})

// Request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // Assuming backend returns a structure with 'code'. 
    // AffairController returns Result (code, message, data).
    // SubscribeController returns ApiResponse (code, message, data).
    // Both use 200 for success.

    if (res.code !== 200) {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service

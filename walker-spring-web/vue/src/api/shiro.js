import request from '@/utils/request'

export function login(data) {
  console.info("login")
  console.info(data)
  return request({
    url: '/shiro/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/shiro/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/shiro/logout',
    method: 'post'
  })
}

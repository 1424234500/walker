import Cookies from 'js-cookie'


export function clear() {
  //console.info("utils/store.js/clear")
  setToken('')
  setUser(null)

}



const KEY_TOKEN = 'TOKEN'

export function getToken() {
  var res = Cookies.get(KEY_TOKEN)
  //console.info("utils/store.js/getToken", res)
  return res
}

export function setToken(token) {
  //console.info("utils/store.js/setToken", token)
  return Cookies.set(KEY_TOKEN, token)
}

const KEY_USER = 'USER'

export function getUser() {
  var res = Cookies.get(KEY_USER)
  //console.info("utils/store.js/getUser", res)
  return JSON.parse(res)
}

export function setUser(user) {
  //console.info("utils/store.js/setUser", user)
  return Cookies.set(KEY_USER, JSON.stringify(user))
}



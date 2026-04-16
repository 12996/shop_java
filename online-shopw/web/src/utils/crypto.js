import CryptoJS from 'crypto-js'

const keyStr = '-mall4j-password' // Exact backend matching key

export function encryptPassword(word) {
  const time = String(Date.now()) // Explicitly cast to string
  const key = CryptoJS.enc.Utf8.parse(keyStr)
  // Backend AES decryption logic specifically requires time + password
  const srcs = CryptoJS.enc.Utf8.parse(time + word) 
  
  const encrypted = CryptoJS.AES.encrypt(srcs, key, { 
    mode: CryptoJS.mode.ECB, 
    padding: CryptoJS.pad.Pkcs7 
  })
  
  return encrypted.toString()
}

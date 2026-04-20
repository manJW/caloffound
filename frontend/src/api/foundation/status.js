import axios from 'axios'

export function fetchFoundationStatus() {
  return axios.get('/foundation/status')
}

export function fetchFoundationSmoke() {
  return axios.get('/foundation/status/smoke')
}

import axios from 'axios'

export function loadCalcSchema(params) {
  return axios.get('/foundation/calc/schema', { params })
}

export function loadCalcTemplate(params) {
  return axios.get('/foundation/calc/template', { params })
}

export function previewCalc(payload) {
  return axios.post('/foundation/calc/preview', payload)
}

export function executeCalc(payload) {
  return axios.post('/foundation/calc/execute', payload)
}

export function executeCalcBatch(payload) {
  return axios.post('/foundation/calc/execute-batch', payload)
}

export function listCalcRecords(params) {
  return axios.get('/foundation/calc/records', { params })
}

export function getCalcRecord(recordId) {
  return axios.get(`/foundation/calc/records/${recordId}`)
}

export function getCalcRecordDetail(recordId) {
  return axios.get(`/foundation/calc/records/${recordId}/detail`)
}

export function exportCalcRecord(recordId, format) {
  return axios.get(`/foundation/calc/records/${recordId}/export`, {
    params: { format }
  })
}

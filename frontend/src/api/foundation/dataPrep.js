import axios from 'axios'

export function listTerrainResources(projectId) {
  return axios.get(`/foundation/projects/${projectId}/terrain-resources`)
}

export function listImportHistory(projectId) {
  return axios.get(`/foundation/projects/${projectId}/import-history`)
}

export function getSurveyPoints(projectId, nodeId) {
  return axios.get(`/foundation/projects/${projectId}/nodes/${nodeId}/survey-points`)
}

export function updateSurveyType(projectId, nodeId, surveyType) {
  return axios.put(`/foundation/projects/${projectId}/nodes/${nodeId}/survey-type`, {
    surveyType
  })
}

export function replaceSurveyPoints(projectId, nodeId, payload) {
  return axios.put(`/foundation/projects/${projectId}/nodes/${nodeId}/survey-points`, payload)
}

export function importSurveyBatch(projectId, payload) {
  return axios.put(`/foundation/projects/${projectId}/survey-points/batch`, payload)
}

export function uploadNodeTif(projectId, nodeId, payload) {
  return axios.put(`/foundation/projects/${projectId}/nodes/${nodeId}/tif`, payload)
}

export function clearNodeTif(projectId, nodeId) {
  return axios.delete(`/foundation/projects/${projectId}/nodes/${nodeId}/tif`)
}

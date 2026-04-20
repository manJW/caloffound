import axios from 'axios'

export function listTowerLibrary(projectId) {
  return axios.get(`/foundation/projects/${projectId}/tower-library`)
}

export function listTowerAssignments(projectId, towerType) {
  return axios.get(`/foundation/projects/${projectId}/tower-library/assignments`, {
    params: { towerType }
  })
}

export function getDefaultTowerBodySuggestion(projectId, towerType) {
  return axios.get(`/foundation/projects/${projectId}/tower-library/default-body`, {
    params: { towerType }
  })
}

export function applyDefaultTowerBody(projectId, towerType) {
  return axios.put(`/foundation/projects/${projectId}/tower-library/default-body`, null, {
    params: { towerType }
  })
}

export function getLegRuleConfig(projectId) {
  return axios.get(`/foundation/projects/${projectId}/tower-library/leg-rule`)
}

export function updateLegRuleConfig(projectId, payload) {
  return axios.put(`/foundation/projects/${projectId}/tower-library/leg-rule`, payload)
}

export function updateTowerAssignment(projectId, nodeId, payload) {
  return axios.put(`/foundation/projects/${projectId}/tower-library/nodes/${nodeId}/assignment`, payload)
}

export function listTowerLegs(projectId, nodeId) {
  return axios.get(`/foundation/projects/${projectId}/tower-library/nodes/${nodeId}/legs`)
}

export function replaceTowerLegs(projectId, nodeId, legs) {
  return axios.put(`/foundation/projects/${projectId}/tower-library/nodes/${nodeId}/legs`, { legs })
}

export function autoFillTowerLegs(projectId, nodeId) {
  return axios.put(`/foundation/projects/${projectId}/tower-library/nodes/${nodeId}/legs/auto-fill`)
}

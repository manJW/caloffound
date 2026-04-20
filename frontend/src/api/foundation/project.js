import axios from 'axios'

export function listFoundationProjects() {
  return axios.get('/foundation/projects')
}

export function getFoundationProject(projectId) {
  return axios.get(`/foundation/projects/${projectId}`)
}

export function getFoundationNode(projectId, nodeId) {
  return axios.get(`/foundation/projects/${projectId}/nodes/${nodeId}`)
}

export function getFoundationRegressionSnapshot(projectId, nodeId) {
  return axios.get(`/foundation/projects/${projectId}/nodes/${nodeId}/regression-snapshot`)
}

export function createFoundationProject(payload) {
  return axios.post('/foundation/projects', payload)
}

export function updateFoundationProject(projectId, payload) {
  return axios.put(`/foundation/projects/${projectId}`, payload)
}

export function deleteFoundationProject(projectId) {
  return axios.delete(`/foundation/projects/${projectId}`)
}

export function createFoundationNode(projectId, payload) {
  return axios.post(`/foundation/projects/${projectId}/nodes`, payload)
}

export function updateFoundationNode(projectId, nodeId, payload) {
  return axios.put(`/foundation/projects/${projectId}/nodes/${nodeId}`, payload)
}

export function deleteFoundationNode(projectId, nodeId) {
  return axios.delete(`/foundation/projects/${projectId}/nodes/${nodeId}`)
}

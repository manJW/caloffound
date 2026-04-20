import axios from 'axios'

export function listSceneNodes(projectId) {
  return axios.get(`/foundation/projects/${projectId}/scene/nodes`)
}

export function loadNodeScene(projectId, nodeId) {
  return axios.get(`/foundation/projects/${projectId}/scene/nodes/${nodeId}`)
}

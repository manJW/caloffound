const state = {
  projectContext: null,
  nodeContext: null
}

const mutations = {
  SET_PROJECT_CONTEXT(state, payload) {
    state.projectContext = payload
  },
  SET_NODE_CONTEXT(state, payload) {
    state.nodeContext = payload
  }
}

const actions = {
  setProjectContext({ commit }, payload) {
    commit('SET_PROJECT_CONTEXT', payload)
  },
  setNodeContext({ commit }, payload) {
    commit('SET_NODE_CONTEXT', payload)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

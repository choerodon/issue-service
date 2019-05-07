import { observable, action, computed } from 'mobx';
import { axios, store } from 'choerodon-front-boot';
import querystring from 'query-string';

@store('StateMachineStore')
class StateMachineStore {

  @observable isLoading = false;
  @observable stateMachine = {};
  @observable configType = 'config_condition';

  @computed get getIsLoading() {
    return this.isLoading;
  }

  @computed get getConfigType() {
    return this.configType;
  }

  @computed get getStateMachine() {
    return this.stateMachine;
  }

  @action setConfigType(type) {
    this.configType = type;
  }

  @action setIsLoading(loading) {
    this.isLoading = loading;
  }

  @action setStateMachine(data) {
    this.stateMachine = data;
  }

  loadStateMachineList = (orgId, sort = { field: 'id', order: 'desc' }, map = {}) => {
    this.setIsLoading(true);
    return axios.get(`/issue/v1/organizations/${orgId}/state_machine?${querystring.stringify(map)}&sort=${sort.field},${sort.order}`).then((data) => {
      // this.setStateList(data);
      this.setIsLoading(false);
      if (data && data.failed) {
        Choerdon.propmt(data.message);
      } else {
        return Promise.resolve(data);
      }
    });
  }

  loadStateMachineDeployById = (orgId, stateId) => axios.get(`/state/v1/organizations/${orgId}/state_machines/with_config_deploy/${stateId}`).then((data) => {
    const res = this.handleProptError(data);
    if (data) {
      this.setStateMachine(data);
    }
    return res;
  });

  loadStateMachineDraftById = (orgId, stateId) => axios.get(`/state/v1/organizations/${orgId}/state_machines/with_config_draft/${stateId}`).then((data) => {
    const res = this.handleProptError(data);
    if (data) {
      this.setStateMachine(data);
    }
    return res;
  });

  createStateMachine = (orgId, map) => axios.post(`/state/v1/organizations/${orgId}/state_machines`, JSON.stringify(map));

  deleteStateMachine = (orgId, stateId) => axios.delete(`/issue/v1/organizations/${orgId}/state_machine/${stateId}`)
    .then(data => this.handleProptError(data));

  updateStateMachine = (orgId, stateId, map) => axios
    .put(`/state/v1/organizations/${orgId}/state_machines/${stateId}`, JSON.stringify(map));

  // 编辑状态机时添加状态
  addStateMachineNode = (orgId, stateMachineId, map) => axios
    .post(`/state/v1/organizations/${orgId}/state_machine_nodes?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  updateStateMachineNode = (orgId, nodeId, stateMachineId, map) => axios
    .put(`/state/v1/organizations/${orgId}/state_machine_nodes/${nodeId}?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  deleteStateMachineNode = (orgId, nodeId, stateMachineId) => axios
    .delete(`/state/v1/organizations/${orgId}/state_machine_nodes/${nodeId}?stateMachineId=${stateMachineId}`);

  checkDeleteNode = (orgId, statusId, stateMachineId) => axios
    .get(`/state/v1/organizations/${orgId}/state_machine_nodes/check_delete?statusId=${statusId}&stateMachineId=${stateMachineId}`);

  // 编辑状态机时添加转换
  addStateMachineTransfer = (orgId, stateMachineId, map) => axios
    .post(`state/v1/organizations/${orgId}/state_machine_transforms?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  updateStateMachineTransfer = (orgId, nodeId, stateMachineId, map) => axios
    .put(`/state/v1/organizations/${orgId}/state_machine_transforms/${nodeId}?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  deleteStateMachineTransfer = (orgId, nodeId, stateMachineId) => axios
    .delete(`/state/v1/organizations/${orgId}/state_machine_transforms/${nodeId}?stateMachineId=${stateMachineId}`);

  getTransferById = (orgId, id) => axios.get(`/state/v1/organizations/${orgId}/state_machine_transforms/${id}`).then(data => this.handleProptError(data));

  getStateById = (orgId, id) => axios.get(`/state/v1/organizations/${orgId}/state_machine_nodes/${id}`).then(data => this.handleProptError(data));

  loadTransferConfigList = (orgId, id, type) => {
    this.setIsLoading(true);
    return axios.get(`/state/v1/organizations/${orgId}/config_codes/${id}?type=${type}`)
      .then((data) => {
        this.setIsLoading(false);
        return this.handleProptError(data);
      });
  };

  addConfig = (orgId, stateMachineId, map) => axios.post(`/state/v1/organizations/${orgId}/state_machine_configs/${stateMachineId}?transform_id=${map.transformId}`, JSON.stringify(map))
    .then(data => this.handleProptError(data));

  deleteConfig = (orgId, id) => axios.delete(`/state/v1/organizations/${orgId}/state_machine_configs/${id}`)
    .then(item => this.handleProptError(item));

  publishStateMachine = (orgId, id) => axios.get(`/state/v1/organizations/${orgId}/state_machines/deploy/${id}`)
    .then(data => this.handleProptError(data));

  deleteDraft = (orgId, id) => axios.delete(`/state/v1/organizations/${orgId}/state_machines/delete_draft/${id}`)
    .then(data => this.handleProptError(data));

  updateCondition = (orgId, id, type) => axios.get(`/state/v1/organizations/${orgId}/state_machine_transforms/update_condition_strategy/${id}?condition_strategy=${type}`)
    .then(data => this.handleProptError(data));

  linkAllToNode = (orgId, id, stateMachineId) => axios.post(`/state/v1/organizations/${orgId}/state_machine_transforms/create_type_all?end_node_id=${id}&state_machine_id=${stateMachineId}`)
    .then(data => this.handleProptError(data));

  deleteAllToNode = (orgId, id) => axios.delete(`/state/v1/organizations/${orgId}/state_machine_transforms/delete_type_all/${id}`)
    .then(data => this.handleProptError(data));

  checkName = (orgId, name) => axios.get(
    `/state/v1/organizations/${orgId}/state_machines/check_name?name=${name}`,
  );

  checkStateName = (orgId, name) => axios.get(
    `/state/v1/organizations/${orgId}/status/check_name?name=${name}`,
  );

  checkTransferName = (orgId, startNodeId, endNodeId, id, name) => axios.get(
    `/state/v1/organizations/${orgId}/state_machine_transforms/check_name?startNodeId=${startNodeId}&endNodeId=${endNodeId}&stateMachineId=${id}&name=${name}`,
  );

  handleProptError = (error) => {
    if (error && error.failed) {
      Choerodon.prompt(error.message);
      return false;
    } else {
      return error;
    }
  }
}

const stateMachineStore = new StateMachineStore();
export default stateMachineStore;

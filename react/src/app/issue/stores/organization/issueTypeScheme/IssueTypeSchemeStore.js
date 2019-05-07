import { observable, action, computed } from 'mobx';
import { axios, store } from 'choerodon-front-boot';

const { height } = window.screen;
@store('IssueTypeSchemeStore')
class IssueTypeSchemeStore {
  @observable isLoading = false;

  @observable createSchemeShow = false;

  @observable schemeList = [];

  @observable scheme = false;

  @observable issueTypeList = [];

  @observable pageInfo = {
    current: 1, total: 0, pageSize: height <= 900 ? 10 : 15,
  };

  @action setPageInfo(page) {
    this.pageInfo.current = page.number + 1;
    this.pageInfo.total = page.totalElements;
    this.pageInfo.pageSize = page.size;
  }

  @computed get getPageInfo() {
    return this.pageInfo;
  }

  @computed get getIsLoading() {
    return this.isLoading;
  }

  @action setIsLoading(loading) {
    this.isLoading = loading;
  }

  @computed get getCreateSchemeShow() {
    return this.createSchemeShow;
  }

  @action setCreateSchemeShow(data) {
    this.createSchemeShow = data;
  }

  @action setSchemeList(data) {
    this.schemeList = data;
  }

  @computed get getSchemeList() {
    return this.schemeList.slice();
  }

  @action setScheme(data) {
    this.scheme = data;
  }

  @computed get getScheme() {
    return this.scheme;
  }

  @action setIssueTypeList(data) {
    this.issueTypeList = data;
  }

  @computed get getIssueTypeList() {
    return this.issueTypeList.slice();
  }

  loadSchemeList = (
    orgId,
    page = this.pageInfo.current - 1,
    pageSize = this.pageInfo.pageSize,
    sort = { field: 'id', order: 'desc' },
    param = {},
  ) => {
    this.setIsLoading(true);
    return axios.post(
      `/issue/v1/organizations/${orgId}/issue_type_scheme/list?page=${page}&size=${pageSize}&sort=${sort.field},${sort.order}`,
      JSON.stringify(param),
    ).then((data) => {
      const res = this.handleProptError(data);
      if (res) {
        this.setSchemeList(data.content);
        const { number, size, totalElements } = data;
        this.setPageInfo({ number, size, totalElements });
      }
      this.setIsLoading(false);
    });
  };

  loadSchemeById = (orgId, id) => axios.get(`/issue/v1/organizations/${orgId}/issue_type_scheme/${id}`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setScheme(res);
    }
    return res;
  });

  createScheme = (orgId, scheme) => axios.post(`/issue/v1/organizations/${orgId}/issue_type_scheme`, JSON.stringify(scheme))
    .then(data => this.handleProptError(data));

  updateScheme = (orgId, id, scheme) => axios.put(`/issue/v1/organizations/${orgId}/issue_type_scheme/${id}`, JSON.stringify(scheme))
    .then(data => this.handleProptError(data));

  checkDelete = (orgId, id) => axios.get(`/issue/v1/organizations/${orgId}/issue_type_scheme/check_delete/${id}`)
    .then(data => this.handleProptError(data));

  checkName = (orgId, name) => axios.get(`/issue/v1/organizations/${orgId}/issue_type_scheme/check_name?name=${name}`)
    .then(data => this.handleProptError(data));

  deleteScheme = (orgId, id) => axios.delete(`/issue/v1/organizations/${orgId}/issue_type_scheme/${id}`)
    .then(data => this.handleProptError(data));

  loadIssueTypes = orgId => axios.get(`/issue/v1/organizations/${orgId}/issue_type/types`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setIssueTypeList(res);
    }
    return res;
  });

  handleProptError = (error) => {
    if (error && error.failed) {
      // Choerodon.prompt(error.message);
      return false;
    } else {
      return error;
    }
  }
}

const issueTypeSchemeStore = new IssueTypeSchemeStore();
export default issueTypeSchemeStore;

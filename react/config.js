const config = {
  server: 'http://api.staging.saas.hand-china.com',
  // server: 'http://10.211.108.232:8080', // 王喆
  // server: 'http://10.211.102.55:8080', // csn
  master: '@choerodon/master',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  resourcesLevel: ['site', 'origanization', 'project', 'user'],
};

module.exports = config;

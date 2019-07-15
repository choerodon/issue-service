const config = {
  server: 'http://api.staging.saas.hand-china.com',
  // server: 'http://10.211.108.232:8080', // 王喆
  master: '@choerodon/master',
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  resourcesLevel: ['site', 'origanization', 'project', 'user'],
};

module.exports = config;

package io.choerodon.issue.infra.utils;

public final class SagaTopic {

    private SagaTopic() {
    }

    public static class Project {
        private Project() {
        }

        //创建项目
        public static final String PROJECT_CREATE = "iam-create-project";
        //task
        public static final String TASK_PROJECT_UPDATE = "issue-create-project";

        public static final String ISSUE_DEMO_PROJECT_CLEAN = "demo-issue-project-clean";

        public static final String DEMO_PROJECT_CLEAN = "demo-project-clean";

    }

    public static class Organization {
        private Organization() {
        }

        //组织服务创建组织
        public static final String ORG_CREATE = "org-create-organization";

        //iam接收创建组织事件的SagaTaskCode
        public static final String TASK_ORG_CREATE = "issue-create-organization";

    }


}

# Issue Service
`Issue Service` is the core service of Choerodon.  

The service is responsible for Issue management.

## Requirements
- Java8
- [Iam Service](https://github.com/choerodon/iam-service.git)
- [State Machine Service](https://github.com/choerodon/state-machine-service.git)
- [MySQL](https://www.mysql.com)

## Installation and Getting Started
1. init database

    ```sql
    CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
    CREATE DATABASE issue_service DEFAULT CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON issue_service.* TO choerodon@'%';
    FLUSH PRIVILEGES;
    ```
2. run command `sh init-local-database.sh`
3. run command as follow or run `AgileServiceApplication` in IntelliJ IDEA

    ```bash
    mvn clean spring-boot:run
    ```

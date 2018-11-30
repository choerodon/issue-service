# Issue Service
`Issue Service` is the core service of Choerodon.  

The service is responsible for managing issue types, priorities, states, and state machines at the organizational level, combining options in the form of scheme, and using scheme with projects to apply attributes to corresponding projects to achieve shared configuration of the solution at the organizational level.

## Features
- **Priority Management**
- **IssueType Management**
- **IssueType Scheme Management**
- **State Machine Management**
- **State Machine Scheme Management**

## Requirements
- Java8
- [Agile Service](https://github.com/choerodon/agile-service.git)
- [State Machine Service](https://github.com/choerodon/state-machine-service.git)
- [Iam Service](https://github.com/choerodon/iam-service.git)
- [MySQL](https://www.mysql.com)

## Installation and Getting Started
1. init database

    ```sql
    CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
    CREATE DATABASE issue_service DEFAULT CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON issue_service.* TO choerodon@'%';
    FLUSH PRIVILEGES;
    ```
1. run command `sh init-local-database.sh`
1. run command as follow or run `IssueServiceApplication` in IntelliJ IDEA

    ```bash
    mvn clean spring-boot:run
    ```

## Dependencies
- `go-register-server`: Register server
- `iam-service`：iam service
- `mysql`: agile_service database
- `api-gateway`: api gateway server
- `gateway-helper`: gateway helper server
- `oauth-server`: oauth server
- `manager-service`: manager service
- `asgard-service`: asgard service
- `state-machine-service`: issue service
- `agile-service`: agile service

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the  [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.

import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncLocaleProvider,
  asyncRouter,
  nomatch,
} from 'choerodon-front-boot';
import './main.scss';

const IssueTypeIndex = asyncRouter(() => import('./organization/issueType'));
const IssueTypeSchemeIndex = asyncRouter(() => import('./organization/issueTypeScheme'));
const StateMachineSchemeIndex = asyncRouter(() => import('./organization/stateMachineScheme'));
const PriorityIndex = asyncRouter(() => import('./organization/priority'));
const IssueTypeScreenSchemes = asyncRouter(() => import('./organization/issueTypeScreenSchemes'));
const StateIndex = asyncRouter(() => import('./organization/state'));
const StateMachineIndex = asyncRouter(() => import('./organization/stateMachine'));

@inject('AppState')
class ISSUEIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <Switch>
          <Route
            path={`${match.url}/state-machine-schemes`}
            component={StateMachineSchemeIndex}
          />
          <Route
            path={`${match.url}/issue-type`}
            component={IssueTypeIndex}
          />
          <Route
            path={`${match.url}/issue-type-schemes`}
            component={IssueTypeSchemeIndex}
          />
          <Route
            path={`${match.url}/priorities`}
            component={PriorityIndex}
          />
          <Route
            path={`${match.url}/issue-type-screen-schemes`}
            component={IssueTypeScreenSchemes}
          />
          <Route
            path={`${match.url}/states`}
            component={StateIndex}
          />
          <Route
            path={`${match.url}/state-machines`}
            component={StateMachineIndex}
          />
          <Route path="*" component={nomatch} />
        </Switch>
      </IntlProviderAsync>
    );
  }
}

export default ISSUEIndex;

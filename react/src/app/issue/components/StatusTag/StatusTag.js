import React, { Component } from 'react';
import './StatusTag.scss';
import { getStageMap } from '../../common/utils';

const stageMap = getStageMap();

class StatusTag extends Component {
  render() {
    const {
      data,
      style,
    } = this.props;
    return (
      <div
        className="c7n-statusTag"
        style={{
          background: (data && stageMap[data.type] && stageMap[data.type].colour) || 'transparent',
          ...style,
        }}
      >
        { (data && data.name) || '' }
      </div>
    );
  }
}
export default StatusTag;

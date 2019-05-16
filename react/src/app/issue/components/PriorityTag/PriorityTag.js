import React, { Component } from 'react';
import './PriorityTag.scss';

class PriorityTag extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    if (nextProps.priority
      && this.props.priority
      && nextProps.priority.id === this.props.priority.id) {
      return false;
    }
    return true;
  }

  render() {
    const { data } = this.props;
    return (
      <div
        className="c7n-priorityTag"
        style={{
          backgroundColor: `${data ? data.colour : '#FFFFFF'}1F`,
          color: data ? data.colour : '#FFFFFF',
        }}
      >
        {data ? data.name : ''}
      </div>
    );
  }
}

export default PriorityTag;
